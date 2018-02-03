package banoun.aneece;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class SpringDataMongoDemoApplication extends SpringBootServletInitializer{

	private static final Logger log = Logger.getGlobal();
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringDataMongoDemoApplication.class);
	}

	public static void main(String[] args) {
		log.info("STARTING..."); 
		SpringApplication.run(SpringDataMongoDemoApplication.class, args);
		log.info("STARTED");
	}
}
