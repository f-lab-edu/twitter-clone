package clone.twitter.config;

import clone.twitter.repository.FollowMapper;
import clone.twitter.repository.TweetMapper;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserMapper;
import clone.twitter.service.TweetDefaultService;
import clone.twitter.service.TweetFanOutService;
import clone.twitter.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class TweetServiceConfig {

    private final TweetRepository tweetRepository;

    private final TweetMapper tweetMapper;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    private final StringBuffer stringBuffer;

    @Bean
    public TweetService tweetService() {
        return new TweetDefaultService(tweetRepository);

//        return new TweetFanOutService(tweetRepository,
//                tweetMapper,
//                followMapper,
//                userMapper,
//                objectRedisTemplate,
//                stringRedisTemplate,
//                stringBuffer);
    }
}
