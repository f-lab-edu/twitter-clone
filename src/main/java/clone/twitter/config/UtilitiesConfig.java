package clone.twitter.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilitiesConfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
