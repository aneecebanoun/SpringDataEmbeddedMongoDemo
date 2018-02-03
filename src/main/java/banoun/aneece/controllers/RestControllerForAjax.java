package banoun.aneece.controllers;

import java.util.HashMap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import banoun.aneece.commands.MongodbOverJmsAjaxRequestBody;


@RestController
public class RestControllerForAjax {

	private static final Logger log = LoggerFactory.getLogger(RestControllerForAjax.class);
	@Autowired
	@Qualifier("dbPerformanceBrokerStatus")
	Map<String, Map<String, String>> dbPerformanceBrokerStatus;
	
	@PostMapping("/mongodbOverJmsAjax")
	public Map<String, String> mongodbOverJmsAjax(MongodbOverJmsAjaxRequestBody ajaxBody) {
		Map<String, String> result = new HashMap<>();
		final String ID = ajaxBody.getId();
		while(dbPerformanceBrokerStatus.get(ID) == null || dbPerformanceBrokerStatus.get(ID).get("STATUS").equals("SENDING")){
			sleep(20);
		}
		result.put("insertionTime", dbPerformanceBrokerStatus.get(ID).get("insertionTime"));
		result.put("queryTime", dbPerformanceBrokerStatus.get(ID).get("queryTime"));
		result.put("deleteTime", dbPerformanceBrokerStatus.get(ID).get("deleteTime"));
		result.put("systemInfo", dbPerformanceBrokerStatus.get(ID).get("systemInfo"));
		dbPerformanceBrokerStatus.remove(ID);
		return result;
	}

	private void sleep(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// L0GeE
			log.debug(e.toString());
		}
	}

}
