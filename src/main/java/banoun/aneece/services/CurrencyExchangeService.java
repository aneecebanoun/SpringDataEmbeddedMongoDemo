package banoun.aneece.services;

import org.springframework.stereotype.Service;

import banoun.aneece.model.CurrencyExchange;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private static final int UPDATE_HOUR = 16;
	private final Map<String, List<CurrencyExchange>> dailyCurrencyExchangeRates = new HashMap<>();
	private final String FX_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
	private String calculatedLastUpdateDate;
	
	private List<String> currencyDetails = Arrays.asList(
			"USD:US dollar", "JPY:Japanese yen", "GBP:Pound sterling",
			"AUD:Australian dollar", "CAD:Canadian dollar", "HKD:Hong Kong dollar",
			"NZD:New Zealand dollar", "SGD:Singaporean dollar", "KRW:South Korean won", 
			"ZAR:South African rand", "EUR:Euro");
	
	private List<String> currencies = currencyDetails.stream()
			.map(currencyDetail -> currencyDetail.split(":")[0].trim()).collect(Collectors.toList());

	private final String DATE_PATTERN = "yyyy-MM-dd";

	public CurrencyExchangeService(){
		try {
			getDailyCurrencyExchange();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			//L0gEe
		}
	}
	
	public Boolean validAmount(String amount){
		try{
			Double.valueOf(amount);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	public List<CurrencyExchange> getDailyCurrencyExchange()
			throws ParserConfigurationException, SAXException, IOException {
		String formattedDay = getFormattedDay();
		List<CurrencyExchange> currencyExchangeRates = dailyCurrencyExchangeRates.get(formattedDay);
		if (currencyExchangeRates == null) {
			currencyExchangeRates = fxLookup();
			dailyCurrencyExchangeRates.clear();
		}
		calculatedLastUpdateDate = formattedDay;
		dailyCurrencyExchangeRates.put(formattedDay, currencyExchangeRates);
		return currencyExchangeRates;
	}
	
	public String getCalculatedLastUpdateDate() throws ParserConfigurationException, SAXException, IOException{
		if(calculatedLastUpdateDate == null){
			getDailyCurrencyExchange();
		}
		return calculatedLastUpdateDate;
	}
	
	public String convert(String from, String to, String inAmount){
		Double amount = Double.valueOf(inAmount);
		if(from.equals(to)){
			return String.format("%.2f", amount);
		}
		Double fromRate = Double.valueOf(getCurrencyExchange(from).getRate());
		Double toRate = Double.valueOf(getCurrencyExchange(to).getRate());
		return String.format("%.2f", amount/fromRate*toRate);
	}
	
	private CurrencyExchange getCurrencyExchange(String currency){
		if(dailyCurrencyExchangeRates.get(getFormattedDay())==null){
			try {
				getDailyCurrencyExchange();
			} catch (ParserConfigurationException | SAXException | IOException e1) {
				return null;
			}
		}
		String day = dailyCurrencyExchangeRates.keySet().stream().collect(Collectors.toList()).get(0);
		return dailyCurrencyExchangeRates.get(day).stream().filter(e -> e.getCurrency().equals(currency)).collect(Collectors.toList()).get(0);
	}
	
	private List<CurrencyExchange> fxLookup() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = (Document) documentBuilder.parse(FX_URL);
		NodeList nodeList = document.getElementsByTagName("*");
		String time = getTime(nodeList);
		return getCurrencyExchanges(nodeList, time);
	}

	private List<CurrencyExchange> getCurrencyExchanges(NodeList nodeList, String time) {
		time = displayDate(time);
		List<CurrencyExchange> currencyExchanges = new ArrayList<>();
		CurrencyExchange currencyExchange = new CurrencyExchange();
		currencyExchange.setCurrency("EUR");
		currencyExchange.setDescription("Euro");
		currencyExchange.setRate("1.0000");
		currencyExchange.setTime(time);
		currencyExchanges.add(currencyExchange);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String currency = node.getAttributes().getNamedItem("currency") != null
					? node.getAttributes().getNamedItem("currency").getNodeValue().trim() : "";
			if (node.getNodeType() == Node.ELEMENT_NODE && currencies.contains(currency)) {
				currencyExchange = new CurrencyExchange();
				currencyExchange.setCurrency(currency);
				String description = currencyDetails.get(currencies.indexOf(currency)).split(":")[1];
				currencyExchange.setDescription(description);
				String rate = node.getAttributes().getNamedItem("rate").getNodeValue().trim();
				rate = String.format("%.4f", Double.valueOf(rate));
				currencyExchange.setRate(rate);
				currencyExchange.setTime(time);
				currencyExchanges.add(currencyExchange);
			}
		}
		return currencyExchanges;
	}

	private String getTime(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getNamedItem("time") != null) {
				return node.getAttributes().getNamedItem("time").getNodeValue().trim();
			}
		}
		return null;
	}

	private Boolean isWeekEnd(LocalDateTime localDate) {
		Boolean earlyMonday = DayOfWeek.MONDAY == localDate.getDayOfWeek() && localDate.getHour() <  UPDATE_HOUR;
		return EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(localDate.getDayOfWeek()) || earlyMonday;
	}

	private LocalDateTime getLastUpdategDay(LocalDateTime localDate) {
		 
		while (isWeekEnd(localDate) || beforeUpdate(localDate)) {
			localDate = localDate.plusDays(-1);
		}
		return localDate;
	}
	
	private Boolean beforeUpdate(LocalDateTime localDate){
		return LocalDateTime.now().getDayOfWeek() == localDate.getDayOfWeek() && localDate.getHour() <  UPDATE_HOUR;
	}

	private String getFormattedDay() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(new StringBuilder(DATE_PATTERN).reverse().toString());
		LocalDateTime day = getLastUpdategDay(LocalDateTime.now());
		return day.format(formatter);
	}

	private String displayDate(String inDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
		DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern(new StringBuilder(DATE_PATTERN).reverse().toString());
		return LocalDate.parse(inDate, formatter).format(formatter_);
	}

}
