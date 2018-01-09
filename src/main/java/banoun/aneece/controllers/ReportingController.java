package banoun.aneece.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import banoun.aneece.services.ReportingService;

@Controller
public class ReportingController {

	@Autowired
	ReportingService reportingService;

	@RequestMapping(value = "/stockFilteredReporting", method = RequestMethod.POST)
	public String stockFilteredReporting(Model model,
			@RequestParam(value="searchKey", required = true) String searchKey, @RequestParam(value="searchType", required = true) String searchType){
		model.addAttribute("stockFilteredReporting", reportingService.runTradeFilteredReporting(searchKey, searchType));
		return "stockFilteredReporting";
	}

	@RequestMapping(value="/toggleColourLink", method = { RequestMethod.GET, RequestMethod.POST })
	public String toggleColourLink(Model model,final HttpServletRequest request){
		Boolean theme = (Boolean)request.getSession().getAttribute("theme");
		theme = theme != null ? !theme : true;
		request.getSession().setAttribute("theme", theme);
		return "redirect:/stockReporting";
	}
	
	@RequestMapping(value={"", "/", "/stockReporting"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String stockReporting(Model model,final HttpServletRequest request, 
			@RequestParam(value="sortingOption", required = false) String sortingOption){
		sortingOption = lastSortingOption(request, sortingOption);
		request.getSession().setAttribute("lastSortingOption", sortingOption);
		model.addAttribute("stockReporting", reportingService.runTradeReporting(sortingOption));
		return "stockReporting";
	}
	
	private String lastSortingOption(final HttpServletRequest request, String sortingOption) {
		if(sortingOption == null && request.getSession().getAttribute("lastSortingOption")==null){
			sortingOption = "Amount";
			reportingService.toggleHeaderFlage(sortingOption);
		}
		if(sortingOption == null && request.getSession().getAttribute("lastSortingOption")!=null){
			sortingOption = (String)request.getSession().getAttribute("lastSortingOption");
			reportingService.toggleHeaderFlage(sortingOption);
		}
		return sortingOption;
	}

}
