package clone.twitter.config;

import clone.twitter.repository.TweetMapper;
import clone.twitter.repository.UserMapper;
import clone.twitter.repository.UserRepository;
import clone.twitter.repository.UserRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class PersistenceConfig {

    private final UserMapper userMapper;

    private final TweetMapper tweetMapper;

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryV1(userMapper);
    }
}
