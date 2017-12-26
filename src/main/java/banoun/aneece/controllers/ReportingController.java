package banoun.aneece.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	public String stockFilteredReporting(Model model, final HttpServletResponse response,
			@RequestParam(value="searchKey", required = true) String searchKey, @RequestParam(value="searchType", required = true) String searchType){
		response.setHeader("Cache-Control", "no-cache");
		model.addAttribute("stockFilteredReporting", reportingService.runTradeFilteredReporting(searchKey, searchType));
		return "stockFilteredReporting";
	}

	@RequestMapping(value={"", "/", "/stockReporting"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String stockReporting(Model model,final HttpServletRequest request, final HttpServletResponse response, 
			@RequestParam(value="sortingOption", required = false) String sortingOption){
		response.setHeader("Cache-Control", "no-cache");
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
