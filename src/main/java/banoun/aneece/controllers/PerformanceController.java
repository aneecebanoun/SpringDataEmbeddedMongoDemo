package banoun.aneece.controllers;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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
		model.addAttribute("systemInfo", getSystemInfo());
		
		return "dbPerformance";
	}
	private String getSystemInfo(){
		OperatingSystemMXBean operatingSystemMXBean =  ManagementFactory.getOperatingSystemMXBean();
		com.sun.management.OperatingSystemMXBean sunOperatingSystemMXBean =(com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		long maxosMaxMemory = sunOperatingSystemMXBean.getTotalPhysicalMemorySize();
		StringBuffer sysInfo = new StringBuffer();
		sysInfo.append(style("ENVIRNMENT INFORMATION<br/>","blue"));
		sysInfo.append(style("MEMORY INFORMATION<br/>","red"));
		sysInfo.append(style("Operating System Max memory: ", "lightblue")+ style(byteToMB(maxosMaxMemory)+" (MB)", "yellow")+ "<br/>");
		sysInfo.append(style("Max memory available to the JVM: ", "lightblue")+ style(byteToMB(Runtime.getRuntime().maxMemory())+" (MB)", "yellow")+ "<br/>");
		sysInfo.append(style("Total amount of free memory available to the JVM: ", "lightblue")+ style(byteToMB(Runtime.getRuntime().freeMemory())+" (MB)", "yellow")+ "<br/>");
		sysInfo.append(style("Total memory currently available to the JVM: ", "lightblue")+ style(byteToMB(Runtime.getRuntime().totalMemory())+" (MB)", "yellow")+ "<br/>");
		//sysInfo.append("Total number of processors or cores available to the JVM: "+ Runtime.getRuntime().availableProcessors()+ "<br/>");
		sysInfo.append(style("PROCESSORS INFORMATION<br/>","red"));
		sysInfo.append(style("Total number of processors or cores available to the JVM: ", "lightblue")+ style(operatingSystemMXBean.getAvailableProcessors()+"", "yellow")+ "<br/>");
		sysInfo.append(style("CPU Architecture: ", "lightblue")+ style(operatingSystemMXBean.getArch()+"", "yellow")+ "<br/>");
		sysInfo.append(style("OPERATING SYSTEM INFORMATION<br/>","red"));
		sysInfo.append(style("OS Name: ", "lightblue")+ style(operatingSystemMXBean.getName()+"", "yellow")+ "<br/>");
		sysInfo.append(style("OS Version: ", "lightblue")+ style(operatingSystemMXBean.getVersion()+"", "yellow")+ "<br/>");
		return sysInfo.toString();
		
	}
	private String byteToMB(long byteMemory){
		return "" + (byteMemory/(1024*1024));
	}
	
	private String style(String text, String color){
		return "<label style='color:" + color +"' >"+ text + "</label>";
	}
}
