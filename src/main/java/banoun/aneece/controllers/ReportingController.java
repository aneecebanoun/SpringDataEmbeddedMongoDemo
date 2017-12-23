package banoun.aneece.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import banoun.aneece.util.TradeReportingUtilityService;


@Controller
public class ReportingController {

	@Autowired
	TradeReportingUtilityService tradeReportingUtilityService;
	
	@RequestMapping(value="/stockReporting", method = { RequestMethod.GET, RequestMethod.POST })
	public String stockReporting(Model model,final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value="sortingOption", required = false) String sortingOption){
		response.setHeader("Cache-Control", "no-cache");
		if(sortingOption == null && request.getSession().getAttribute("lastSortingOption")==null){
			sortingOption = "Amount";
			tradeReportingUtilityService.toggleHeaderFlage(sortingOption);
		}
		if(sortingOption == null && request.getSession().getAttribute("lastSortingOption")!=null){
			sortingOption = (String)request.getSession().getAttribute("lastSortingOption");
			tradeReportingUtilityService.toggleHeaderFlage(sortingOption);
		}
		request.getSession().setAttribute("lastSortingOption", sortingOption);
		model.addAttribute("stockReporting", tradeReportingUtilityService.runTradeReporting(sortingOption));
		return "stockReporting";
	}
	
	@RequestMapping("/stockFilteredReporting/{filter}")
	public String stockFilteredReporting(Model model, @PathVariable("filter") String filter, final HttpServletResponse response){
		response.setHeader("Cache-Control", "no-cache");
		model.addAttribute("stockFilteredReporting", tradeReportingUtilityService.runTradeFilteredReporting(filter));
		return "stockFilteredReporting";
	}

}
