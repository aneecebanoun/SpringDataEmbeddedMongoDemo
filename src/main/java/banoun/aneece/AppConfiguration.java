package banoun.aneece;

import java.io.IOException;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.web.client.RestTemplate;

import com.mongodb.MongoClient;
import banoun.aneece.filters.ViewFilter;
import banoun.aneece.model.TradeEntryListener;
import banoun.aneece.servlets.ChartViewServlet;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@Configuration
@ComponentScan
@EnableJms
public class AppConfiguration extends AbstractMongoConfiguration {

	public final static String DB_PERFORMANCE_MESSAGE_QUEUE = "db-performance-message-queue";

	@Value("${bind.ip}")
	private String mongoURL;

	@Value("${db.name}")
	private String mongoDB_Name;

	@Bean
	public TradeEntryListener radeEntryListener() {
		return new TradeEntryListener();
	}

	@Bean
	public ActiveMQTopic simpleTopic() {
		return new ActiveMQTopic(DB_PERFORMANCE_MESSAGE_QUEUE);
	}

	@Bean
	public EmbeddedMongoFactoryBean embeddedMongoFactoryBean() {
		EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
		mongo.setBindIp(mongoURL);
		return mongo;
	}

	@Bean
	@Override
	public MongoClient mongoClient() {
		try {
			return embeddedMongoFactoryBean().getObject();
		} catch (IOException e) {
			return null;
		}
	}

	@Bean
	public Filter mainFilter() {
		return new ViewFilter();
	}

	@Bean("dbPerformanceBrokerStatus")
	public Map<String, Map<String, String>> dbPerformanceBrokerStatus() {
		Map<String, Map<String, String>> brokersStatus = new ConcurrentHashMap<>();
		return brokersStatus;
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// factory.setPubSubDomain(false);
		// factory.setSubscriptionDurable(true);
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Override
	protected String getDatabaseName() {
		return mongoDB_Name;
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restClient = new RestTemplate(
				new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		restClient.setInterceptors(
				Collections.singletonList((request, body, execution) -> execution.execute(request, body)));
		return restClient;
	}
	
	@Bean
	public ChartViewServlet chartViewServlet(){
		return new ChartViewServlet();
	}
	
	@Bean
	public ServletRegistrationBean<Servlet> servletRegistrationBean(){
	    return new ServletRegistrationBean<>(chartViewServlet(),"/chartView");
	}

}