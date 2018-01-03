package banoun.aneece;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import com.mongodb.MongoClient;
import banoun.aneece.filters.MainFilter;
import banoun.aneece.model.TradeEntryListener;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@Configuration
@ComponentScan
public class AppConfiguration {

	public final static String DB_PERFORMANCE_MESSAGE_QUEUE = "db-performance-message-queue";
	
	@Value("${bind.ip}")
    private String mongoURL;
	
	@Value("${db.name}")
    private String mongoDB_Name;
	
	@Bean
	public TradeEntryListener radeEntryListener(){
		return new TradeEntryListener();
	}
	
    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
        mongo.setBindIp(mongoURL);
        MongoClient mongoClient = mongo.getObject();
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, mongoDB_Name);
        return mongoTemplate;
    }
    
    @Bean
    public Filter mainFilter(){
    	return new MainFilter();
    }
    
	@Bean("dbPerformanceBrokerStatus")
	public Map<String, Map<String, String>> dbPerformanceBrokerStatus(){
		Map<String, Map<String, String>> brokersStatus = new ConcurrentHashMap<>();
		return brokersStatus;
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}
    
}