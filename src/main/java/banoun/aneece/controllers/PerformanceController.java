package banoun.aneece.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import banoun.aneece.jms.producers.JmsDbPerformanceProducerService;
import banoun.aneece.services.DbPerformanceService;

@Controller
public class PerformanceController {

	@Autowired
	DbPerformanceService dbPerformanceService;
	@Autowired
	JmsDbPerformanceProducerService jmsDbPerformanceProducerService;
	@Autowired
	@Qualifier("dbPerformanceBrokerStatus")
	Map<String, Map<String, String>> dbPerformanceBrokerStatus;

	
	@RequestMapping("/dbPerformance")
	public String dbPerformance(Model model){
		Map<String, String> testResult = dbPerformanceService.mongoDbTestDrive(300);
		model.addAttribute("insertionTime", testResult.get("insertionTime"));
		model.addAttribute("queryTime", testResult.get("queryTime"));
		model.addAttribute("deleteTime", testResult.get("deleteTime"));
		model.addAttribute("systemInfo", testResult.get("systemInfo"));
		return "dbPerformance";
	}
	
	@RequestMapping(value={"/mongodbOverJmsTest"}, method = RequestMethod.GET)
	public String mongodbOverJmsTest(Model model){
		String brokerId = jmsDbPerformanceProducerService.getUniqueKey(dbPerformanceBrokerStatus);
		jmsDbPerformanceProducerService.sendMessage(brokerId);
		model.addAttribute("brokerId", brokerId);
		return "mongodbOverJmsTest";
	}
	
}
