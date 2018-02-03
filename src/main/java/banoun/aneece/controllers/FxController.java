package banoun.aneece.controllers;

import static banoun.aneece.services.CurrencyExchangeServiceUtilities.*;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import banoun.aneece.model.CurrencyExchange;
import banoun.aneece.services.CurrencyExchangeService;

@Controller
public class FxController {
	
	@Autowired
	private CurrencyExchangeService currencyExchangeService;
	
	@RequestMapping(value = {"","/", "/fxRates"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String fxRates(final Model model,
			final @RequestParam(value="fromCurrency", required = false) String fromCurrency,
			final @RequestParam(value="toCurrency", required = false) String toCurrency,
			final @RequestParam(value="amount", required = false) String amount, final HttpServletRequest request) throws ParserConfigurationException, SAXException, IOException 
					{
		currencyConversionSection(model, fromCurrency, toCurrency, amount);
		chartSections(model, request);
		return "fxEu";
	}

	private void chartSections(final Model model, final HttpServletRequest request) {
		final String baseUrl = request.getRequestURL().substring(0, request.getRequestURL().length()
				- request.getRequestURI().length() + request.getContextPath().length()) + "/";

		final String chartViewUrl = baseUrl + "chartView";
		
		//multi line chart trends
		final String gbpParams = "?currencies=CAD&currencies=EUR&currencies=USD&inCurrency=GBP";
		final String usParams = "?currencies=CAD&currencies=EUR&currencies=GBP&inCurrency=USD";
		final String gbpChartUrl = baseUrl + "chartView"+gbpParams;
		final String usChartUrl = baseUrl + "chartView"+usParams;
		model.addAttribute("gbpChartUrl", gbpChartUrl);
		model.addAttribute("usChartUrl", usChartUrl);

		//calculate currency trends based on other currency 
		final String againstA1Params = "?currencies=USD&inCurrency=GBP";
		final String againstB1Params = "?currencies=GBP&inCurrency=USD";
		final String againstA1Url = baseUrl + "chartView"+againstA1Params;
		final String againstB1Url = baseUrl + "chartView"+againstB1Params;
		model.addAttribute("againstA1Url", againstA1Url);
		model.addAttribute("againstB1Url", againstB1Url);

		//chartHistory
		final String chartHistory1Url = chartViewUrl+"?currencies=GBP"; 
		final String chartHistory2Url = chartViewUrl+"?currencies=USD";
		model.addAttribute("chartHistory1Url", chartHistory1Url);
		model.addAttribute("chartHistory2Url", chartHistory2Url);

		model.addAttribute("chartViewUrl", chartViewUrl);
	}

	private void currencyConversionSection(Model model, String fromCurrency, String toCurrency, String amount) throws ParserConfigurationException, SAXException, IOException {
		final String[] ecbLink = {"'https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml'", "'_blank'"};
		String fromSelect = "GBP";
		String toSelect = "EUR";
		String originalAmount = "";
		String convertedAmount = "     ";
		if(!StringUtils.isEmpty(fromCurrency)){
			fromSelect = fromCurrency;
			toSelect = toCurrency;
			if(validAmount(amount)){
				originalAmount = "("+fromSelect + ") "+amount + " = ("+toSelect+") " + currencyExchangeService.convert(fromSelect, toSelect, amount);
 			}else{
				originalAmount = amount;
			}
		}
		String size = 20 < originalAmount.length()? (originalAmount.length()+2)+"":"20";
		model.addAttribute("fromSelect", fromSelect);
		model.addAttribute("toSelect", toSelect);
		model.addAttribute("originalAmount", originalAmount);
		model.addAttribute("convertedAmount", convertedAmount);
		model.addAttribute("ecbLink", ecbLink);
		model.addAttribute("size", size);
		final List<CurrencyExchange> currencyExchanges = currencyExchangeService.getDailyCurrencyExchange();
		model.addAttribute("dailyCurrencyExchange", currencyExchanges);
		model.addAttribute("calculatedLastUpdateDate", calculatedLastUpdateDate);
	}
}
