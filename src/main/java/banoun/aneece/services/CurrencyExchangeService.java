package banoun.aneece.services;
import static banoun.aneece.services.CurrencyExchangeServiceUtilities.*;

import org.springframework.stereotype.Service;
import banoun.aneece.model.CurrencyExchange;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class CurrencyExchangeService {

	public List<CurrencyExchange> getDailyCurrencyExchange()
			throws ParserConfigurationException, SAXException, IOException {
		final String formattedDay = getFormattedDay();
		List<CurrencyExchange> currencyExchangeRates = dailyCurrencyExchangeRates.get(formattedDay);
		if (withinUpdateTime() || currencyExchangeRates == null) {
			currencyExchangeRates = fxLookup();
			dailyCurrencyExchangeRates.clear();
		}
		dailyCurrencyExchangeRates.put(formattedDay, currencyExchangeRates);
		calculatedLastUpdateDate = formattedDay;
		return currencyExchangeRates;
	}
	
	public String getCalculatedLastUpdateDate() throws ParserConfigurationException, SAXException, IOException{
		getDailyCurrencyExchange();
		return calculatedLastUpdateDate;
	}
	
	public String convert(final CurrencyExchange from, final CurrencyExchange to, final String inAmount){
		final Double amount = Double.valueOf(inAmount);
		
		if(isNull(from, to) || from.getCurrency().equals(to.getCurrency())){
			return String.format("%.2f", amount);
		}
		final Double fromRate = Double.valueOf(from.getRate());
		final Double toRate = Double.valueOf(to.getRate());
		return String.format("%.8f", amount/fromRate*toRate);
	}
	
	public String convert(final String from, final String to, final String inAmount){
		final Double amount = Double.valueOf(inAmount);
		if(from.equals(to)){
			return String.format("%.2f", amount);
		}
		final Double fromRate = Double.valueOf(getCurrencyExchange(from).getRate());
		final Double toRate = Double.valueOf(getCurrencyExchange(to).getRate());
		return String.format("%.2f", amount/fromRate*toRate);
	}
	
	public CurrencyExchange getCurrencyExchange(final List<CurrencyExchange> currencyExchanges, final String currency){
		final List<CurrencyExchange> cExchanges = isNull(currencyExchanges)?new ArrayList<CurrencyExchange>():
			currencyExchanges.stream().filter(e -> !isNull(e, currency)&&!isNull(e.getCurrency())&&currency.equals(e.getCurrency())).collect(Collectors.toList());
		return cExchanges.isEmpty()? null : cExchanges.get(0);
	}
	
	private CurrencyExchange getCurrencyExchange(final String currency){
		if(dailyCurrencyExchangeRates.get(getFormattedDay())==null){
			try {
				getDailyCurrencyExchange();
			} catch (ParserConfigurationException | SAXException | IOException e1) {
				return null;
			}
		}
		final String day = dailyCurrencyExchangeRates.keySet().stream().collect(Collectors.toList()).get(0);
		return dailyCurrencyExchangeRates.get(day).stream().filter(e -> e.getCurrency().equals(currency)).collect(Collectors.toList()).get(0);
	}
	
	private List<CurrencyExchange> fxLookup() throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final Document document = (Document) documentBuilder.parse(FX_URL);
		final NodeList nodeList = document.getElementsByTagName("*");
		final String time = getDataFileTimeStamp(nodeList);
		return getCurrencyExchanges(nodeList, time);
	}

	private Boolean outDated(){
		final List<String> sortedkeys = currencyExchangeHistory.keySet().stream().sorted((o1, o2) -> o2.compareTo(o1)).collect(Collectors.toList());
		return sortedkeys.size() == 0? true : textToLocalDate(sortedkeys.get(0), DATE_PATTERN).isBefore(LocalDate.now().minusDays(4));
	}
	
	public Map<String, List<CurrencyExchange>> getFxHistoryLookup() throws SAXException, IOException, ParserConfigurationException{
		if(currencyExchangeHistory != null && !currencyExchangeHistory.isEmpty() &&!outDated()){
			return currencyExchangeHistory;
		}
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		final Document document = (Document) documentBuilder.parse(FX_HISTORY_URL);
		final NodeList nodeList = document.getElementsByTagName("*");
		return getCurrencyExchangeHistory(nodeList);
	}

	private List<CurrencyExchange> getCurrencyExchanges(final NodeList nodeList, final String intime) {
		List<CurrencyExchange> currencyExchanges = new ArrayList<>();
		if(intime==null){
			return currencyExchanges;
		}
		final String time = displayDate(intime);
		currencyExchanges.add(new CurrencyExchange(time, "EUR", "1.0000", "Euro"));
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			final String currency = node.getAttributes().getNamedItem("currency") != null
					? node.getAttributes().getNamedItem("currency").getNodeValue().trim() : "";
			if (node.getNodeType() == Node.ELEMENT_NODE && currencies.contains(currency)) {
				currencyExchanges.add(getCurrencyExchangeFromNode(node, currency, time));
			}
		}
		return currencyExchanges;
	}
	
	private CurrencyExchange getCurrencyExchangeFromNode(final Node node, final String currency, final String time){
		final String description = currencyDetails.get(currencies.indexOf(currency)).split(":")[1];
		final String rate = node.getAttributes().getNamedItem("rate").getNodeValue().trim();
		final String formattedRate = String.format("%.4f", Double.valueOf(rate));
		return new CurrencyExchange(time, currency, formattedRate, description); 
	}
	
	private String getDataFileTimeStamp(final NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getNamedItem("time") != null) {
				return node.getAttributes().getNamedItem("time").getNodeValue().trim();
			}
		}
		return null;
	}

	public Map<String, List<CurrencyExchange>> getCurrencyExchangeHistory(final NodeList nodeList) {
		currencyExchangeHistory = new ConcurrentHashMap<>(); 
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getNamedItem("time") != null) {
				final String time = node.getAttributes().getNamedItem("time").getNodeValue().trim();
				currencyExchangeHistory.put(time, getCurrencyExchanges(node.getChildNodes(), time));
			}
		}
		return currencyExchangeHistory;
	}

}
