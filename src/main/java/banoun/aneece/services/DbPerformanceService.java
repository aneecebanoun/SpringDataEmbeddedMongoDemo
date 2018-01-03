package banoun.aneece.services;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import banoun.aneece.model.TradeEntry;
import banoun.aneece.model.Trader;
import banoun.aneece.repositories.TradeEntryRepository;
import banoun.aneece.repositories.TraderRepository;

@Service
public class DbPerformanceService {
	
	@Autowired
	ReportingDataService reportingDataService;
	@Autowired
	TradeEntryRepository tradeEntryRepository;
	@Autowired
	TraderRepository tradeRRepository;

	public Map<String, String> mongoDbTestDrive(final int ROWS){
		Map<String, String> result = new ConcurrentHashMap<>();
		long start = System.currentTimeMillis();
		List<TradeEntry> tradeEntryList = reportingDataService.loadData(ROWS);
		long end = System.currentTimeMillis();
		String insertionTime = "Time to INSERT (" +tradeEntryList.size()+") RECORD takes: "+reportingDataService.timing(start, end);
		start = System.currentTimeMillis();
		List<Trader> traders = tradeEntryList.stream().parallel().map(tradeEntry -> tradeEntry.getTrader()).collect(Collectors.toList());
		List<TradeEntry> qtradeEntryList = new ArrayList<>(); 
		tradeEntryRepository.findByTraderInOrderByAmountDesc(traders).parallel().forEach(qtradeEntryList::add);
		end = System.currentTimeMillis();
		String queryTime = "Time to QUERY (" +qtradeEntryList.size()+") RECORD takes: "+reportingDataService.timing(start, end);
		start = System.currentTimeMillis();
		tradeEntryRepository.deleteAll(tradeEntryList);
		tradeRRepository.deleteAll(traders);
		end = System.currentTimeMillis();
		String deleteTime = "Time to DELETE (" +tradeEntryList.size()+") RECORD takes: "+reportingDataService.timing(start, end);
		result.put("insertionTime", insertionTime);
		result.put("queryTime", queryTime);
		result.put("deleteTime", deleteTime);
		result.put("systemInfo", getSystemInfo());
		return result;
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
