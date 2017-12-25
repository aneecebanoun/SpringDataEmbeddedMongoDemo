package banoun.aneece;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

import banoun.aneece.model.TradeEntryListener;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@Configuration
@ComponentScan
public class MvcConfiguration{
	
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

    
}