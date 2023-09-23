package clone.twitter.config;

import clone.twitter.repository.FanOutRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import clone.twitter.service.TweetFanOutService;
import clone.twitter.service.TweetService;
import clone.twitter.service.UserService;
import clone.twitter.service.UserServiceWithSession;
import clone.twitter.session.SessionStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class ServiceConfig {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionStorage sessionStorage;

    private final TweetRepository tweetRepository;
    private final FanOutRepository fanOutRepository;

    @Bean
    public UserService userService() {
        return new UserServiceWithSession(userRepository, passwordEncoder, sessionStorage);
    }

    @Bean
    public TweetService tweetService() {
        return new TweetFanOutService(tweetRepository, fanOutRepository);
    }
}
