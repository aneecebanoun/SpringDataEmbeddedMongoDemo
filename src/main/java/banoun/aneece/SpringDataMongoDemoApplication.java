package banoun.aneece;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringDataMongoDemoApplication extends SpringBootServletInitializer{

	   @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(SpringDataMongoDemoApplication.class);
	    }

	public static void main(String[] args) {
		SpringApplication.run(SpringDataMongoDemoApplication.class, args);
	}
}
