package clone.twitter;

import clone.twitter.config.PersistenceConfig;
import clone.twitter.config.UtilitiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@EnableCaching
@Import({PersistenceConfig.class, UtilitiesConfig.class})
@SpringBootApplication
public class TwitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterApplication.class, args);
	}
}
