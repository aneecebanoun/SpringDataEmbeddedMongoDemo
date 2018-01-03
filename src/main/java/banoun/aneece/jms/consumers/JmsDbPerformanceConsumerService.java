package banoun.aneece.jms.consumers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import banoun.aneece.AppConfiguration;
import banoun.aneece.services.DbPerformanceService;
@Component
public class JmsDbPerformanceConsumerService {

	@Autowired
	DbPerformanceService dbPerformanceService;

	@Autowired
	@Qualifier("dbPerformanceBrokerStatus")
	Map<String, Map<String, String>> dbPerformanceBrokerStatus;

	@JmsListener(destination = AppConfiguration.DB_PERFORMANCE_MESSAGE_QUEUE, containerFactory = "jmsFactory")
	public void receiveMessage(String id) {
		Map<String, String> testResult = dbPerformanceService.mongoDbTestDrive(300);
		dbPerformanceBrokerStatus.get(id).put("insertionTime", testResult.get("insertionTime"));
		dbPerformanceBrokerStatus.get(id).put("queryTime", testResult.get("queryTime"));
		dbPerformanceBrokerStatus.get(id).put("deleteTime", testResult.get("deleteTime"));
		dbPerformanceBrokerStatus.get(id).put("systemInfo", testResult.get("systemInfo"));
		dbPerformanceBrokerStatus.get(id).put("STATUS", "DONE");
	}

}
