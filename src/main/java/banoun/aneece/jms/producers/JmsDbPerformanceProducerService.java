package banoun.aneece.jms.producers;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import banoun.aneece.AppConfiguration;
import banoun.aneece.services.ReportingConsoleViewService;

@Component
public class JmsDbPerformanceProducerService {
    
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	@Qualifier("dbPerformanceBrokerStatus")
	Map<String, Map<String, String>> dbPerformanceBrokerStatus;

    public void sendMessage(String id) {
        Map<String, String> messageMap = new ConcurrentHashMap<>();
        messageMap.put("STATUS", "SENDING");
        dbPerformanceBrokerStatus.put(id, messageMap);
//        jmsTemplate.setDefaultDestinationName(AppConfiguration.DB_PERFORMANCE_MESSAGE_QUEUE);
//        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.convertAndSend(AppConfiguration.DB_PERFORMANCE_MESSAGE_QUEUE, id);
    }
    
    public String getUniqueKey(Map<String, Map<String, String>> map){
    	String key = ReportingConsoleViewService.getRandomSequence(9);
    	while(map.containsKey(key)){
    		key = ReportingConsoleViewService.getRandomSequence(9);
    	}
    	return key;
    }

}
