package banoun.aneece.controllers;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import banoun.aneece.services.CurrencyExchangeService;

@Controller
public class FxController {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	CurrencyExchangeService currencyExchangeService;
	//test circlci
	@RequestMapping(value = "/fxRates", method = { RequestMethod.GET, RequestMethod.POST })
	public String fxRates(Model model,
			@RequestParam(value="fromCurrency", required = false) String fromCurrency,
			@RequestParam(value="toCurrency", required = false) String toCurrency,
			@RequestParam(value="amount", required = false) String amount) 
					throws ParserConfigurationException, SAXException, IOException {
		String[] ecbLink = {"'https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml'", "'_blank'"};
		String fromSelect = "GBP";
		String toSelect = "EUR";
		String originalAmount = "";
		String convertedAmount = "     ";
		
		if(!StringUtils.isEmpty(fromCurrency)){
			fromSelect = fromCurrency;
			toSelect = toCurrency;
			if(currencyExchangeService.validAmount(amount)){
				convertedAmount = "Coverted Value: "+currencyExchangeService.convert(fromSelect, toSelect, amount);
			}
			originalAmount = amount;
		}
		model.addAttribute("fromSelect", fromSelect);
		model.addAttribute("toSelect", toSelect);
		model.addAttribute("originalAmount", originalAmount);
		model.addAttribute("convertedAmount", convertedAmount);
		model.addAttribute("ecbLink", ecbLink);
		model.addAttribute("calculatedLastUpdateDate", currencyExchangeService.getCalculatedLastUpdateDate());
		model.addAttribute("dailyCurrencyExchange", currencyExchangeService.getDailyCurrencyExchange());
		return "fxEu";
	}
}
