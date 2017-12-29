package banoun.aneece.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import banoun.aneece.model.TradeEntry;
import banoun.aneece.repositories.TradeEntryRepository;
import banoun.aneece.repositories.TraderRepository;
import banoun.aneece.services.ReportingDataService;

@Controller
public class PerformanceController {

	@Autowired
	ReportingDataService reportingDataService;
	@Autowired
	TradeEntryRepository tradeEntryRepository;
	@Autowired
	TraderRepository traderRepository;
	
	@RequestMapping("/dbPerformance")
	public String dbPerformance(Model model,final HttpServletRequest request, final HttpServletResponse response){
		model.addAttribute("loadTime", reportingDataService.getDataLoadingTime());
		long start = System.currentTimeMillis();
		List<TradeEntry> tradeEntryList = new ArrayList<>(); 
		tradeEntryRepository.findAll().forEach(tradeEntryList::add);
		long end = System.currentTimeMillis();
		String queryTime = "Time to QUERY (" +tradeEntryList.size()+") RECORD takes: "+reportingDataService.timing(start, end);
		model.addAttribute("queryTime", queryTime);
		return "dbPerformance";
	}
}
