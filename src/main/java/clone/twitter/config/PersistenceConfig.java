package clone.twitter.config;

import clone.twitter.repository.FanOutDefaultRepository;
import clone.twitter.repository.FanOutRepository;
import clone.twitter.repository.FollowMapper;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.FollowRepositoryV1;
import clone.twitter.repository.LikeTweetMapper;
import clone.twitter.repository.LikeTweetRepository;
import clone.twitter.repository.LikeTweetRepositoryV1;
import clone.twitter.repository.TweetMapper;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.TweetRepositoryV1;
import clone.twitter.repository.UserMapper;
import clone.twitter.repository.UserRepository;
import clone.twitter.repository.UserRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Configuration
public class PersistenceConfig {

    private final UserMapper userMapper;
    private final TweetMapper tweetMapper;
    private final FollowMapper followMapper;
    private final LikeTweetMapper likeTweetMapper;

    private final RedisTemplate<String, Object> objectFanOutRedisTemplate;
    private final RedisTemplate<String, String> stringFanOutRedisTemplate;

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryV1(userMapper);
    }

    @Bean
    public TweetRepository tweetRepository() {
        return new TweetRepositoryV1(tweetMapper);
    }

    @Bean
    public FollowRepository followRepository() {
        return new FollowRepositoryV1(followMapper);
    }

    @Bean
    public LikeTweetRepository likeTweetRepository() {
        return new LikeTweetRepositoryV1(likeTweetMapper);
    }

    @Bean
    public FanOutRepository fanOutRepository() {
        return new FanOutDefaultRepository(tweetMapper, objectFanOutRedisTemplate,
            stringFanOutRedisTemplate);
    }
}
