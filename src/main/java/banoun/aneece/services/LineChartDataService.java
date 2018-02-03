package banoun.aneece.services;
import static banoun.aneece.services.CurrencyExchangeServiceUtilities.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.xml.sax.SAXException;

import banoun.aneece.model.CurrencyExchange;

@Service
@RequestScope
public class LineChartDataService {

	@Autowired
	public CurrencyExchangeService currencyService;

	public Map<String, Object> currenciesDatasets(final String... currencies) {
		final Map<String, Object> dataSetInfos = new HashMap<>();
		final TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		final Map<String, TimeSeries> currenciesTimeSeries = new HashMap<>();
		final Map<String, String> title = new HashMap<>();
		getSortedkeys().stream().forEach(time -> {
			final Integer year = Integer.parseInt(time.split("-")[0]);
			final Integer month = Integer.parseInt(time.split("-")[1]);
			final Integer day = Integer.parseInt(time.split("-")[2]);
			for (final String currency : currencies) {
				final CurrencyExchange currencyExchange = currencyService
						.getCurrencyExchange(getFxHistory().get(time), currency);
				final CurrencyExchange usCurrencyExchange = currencyService
						.getCurrencyExchange(getFxHistory().get(time), "USD");
				final String cRate = "EUR".equals(currency) ? 
						currencyService.convert(usCurrencyExchange, currencyExchange, "1.00") : isNull(currencyExchange)?"0.00":currencyExchange.getRate();
			    title.put(currencyExchange.getCurrency(), currencyExchange.getCurrency());
			    final Double rate = Double.parseDouble(cRate);
			    TimeSeries series = currenciesTimeSeries.get(currency);
				if (series == null) {
					series = new TimeSeries(currencyExchange.getDescription());
					currenciesTimeSeries.put(currency, series);
					timeSeriesCollection.addSeries(series);
				}
				series.add(new Day(day, month, year), rate);
			}
		});
		final StringBuffer chartTitle = new StringBuffer("Currency History for:");
		final String startingChartTitle = chartTitle.toString(); 
		title.keySet().stream().forEach(cKey->chartTitle.append(startingChartTitle.equals(chartTitle.toString())? " "+title.get(cKey) : ", "+title.get(cKey)));
		dataSetInfos.put("dataSets", timeSeriesCollection);
		dataSetInfos.put("chartTitle", chartTitle.toString());
		return dataSetInfos;
	}
	
	public Map<String, Object> currencyPerformanceDatasets(final String inCurrency, final String... currencies) {
		final Map<String, Object> dataSetInfos = new HashMap<>();
		final TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		final Map<String, String> title = new HashMap<>();
		final Map<String, TimeSeries> currenciesTimeSeries = new HashMap<>();
		getSortedkeys().stream().forEach(time -> {
			final Integer year = Integer.parseInt(time.split("-")[0]);
			final Integer month = Integer.parseInt(time.split("-")[1]);
			final Integer day = Integer.parseInt(time.split("-")[2]);
			for (final String currency : currencies) {
				if(currency.equals(inCurrency)){
					continue;
				}
				final CurrencyExchange currencyExchange = currencyService
						.getCurrencyExchange(getFxHistory().get(time), currency);
				final CurrencyExchange inCurrencyExchange = currencyService
						.getCurrencyExchange(getFxHistory().get(time), inCurrency);
				title.put("main", inCurrencyExchange==null?"EUR":inCurrencyExchange.getCurrency());
				title.put(currencyExchange.getCurrency(), currencyExchange.getCurrency());
				final String cRate = currencyService.convert(inCurrencyExchange, currencyExchange, "1.00"); 
				final Double rate = Double.parseDouble(cRate);
				TimeSeries series = currenciesTimeSeries.get(currency);
				if (series == null) {
					series = new TimeSeries(inCurrencyExchange.getCurrency()+ " against "+ currencyExchange.getCurrency());
					currenciesTimeSeries.put(currency, series);
					timeSeriesCollection.addSeries(series);
				}
				series.add(new Day(day, month, year), rate);
			}
		});
		final StringBuffer chartTitle = title.get("main")==null?new StringBuffer(inCurrency+"=="+inCurrency):new StringBuffer(title.get("main")+" against:");
		final String startingChartTitle = chartTitle.toString();
		title.keySet().stream().filter(cKey->!"main".equals(cKey)).forEach(cKey-> chartTitle.append(startingChartTitle.equals(chartTitle.toString())? " "+title.get(cKey) : ", "+title.get(cKey)));
		dataSetInfos.put("dataSets", timeSeriesCollection);
		dataSetInfos.put("chartTitle", chartTitle.toString());
		return dataSetInfos;
	}

	private  Map<String, List<CurrencyExchange>> getFxHistory(){
		try {
			return currencyService.getFxHistoryLookup();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			return null;
		}
	}
	
	private List<String> getSortedkeys(){
		return getFxHistory().keySet().stream().sorted((o1, o2) -> o2.compareTo(o1)).collect(Collectors.toList());
	}

}
