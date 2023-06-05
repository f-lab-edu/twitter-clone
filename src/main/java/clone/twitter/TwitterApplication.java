package clone.twitter;

import clone.twitter.config.PersistenceConfig;
import clone.twitter.config.UtilitiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Slf4j
@Import({PersistenceConfig.class, UtilitiesConfig.class})
@SpringBootApplication
public class TwitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterApplication.class, args);
	}
}
