package clone.twitter.config;

import clone.twitter.repository.MemberMapper;
import clone.twitter.repository.TweetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PersistenceConfig {

    private final MemberMapper memberMapper;
    private final TweetMapper tweetMapper;
}
