package clone.twitter.config;

import clone.twitter.repository.FanOutRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.service.TweetFanOutService;
import clone.twitter.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ServiceConfig {

    private final TweetRepository tweetRepository;
    private final FanOutRepository fanOutRepository;

    @Bean
    public TweetService tweetService() {
        return new TweetFanOutService(tweetRepository, fanOutRepository);
    }
}
