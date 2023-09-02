package clone.twitter.config;

import clone.twitter.event.TweetEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class EventConfig {

    private final RedisConnectionFactory redisFanOutConnectionFactory;

    private final TweetEventListener tweetEventListener;

    @Bean
    MessageListenerAdapter fanOutTweetListenerAdapter() {
        return new MessageListenerAdapter(tweetEventListener, "handleFanOutTweetMessage");
    }

    @Bean
    MessageListenerAdapter deleteFanOutTweetListenerAdapter() {
        return new MessageListenerAdapter(tweetEventListener, "handleDeleteFanOutTweetMessage");
    }

    // message listener container(+connection factory)
    @Bean
    public RedisMessageListenerContainer container() {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(redisFanOutConnectionFactory);
        container.addMessageListener(fanOutTweetListenerAdapter(), new PatternTopic("NEW_TWEET"));
        container.addMessageListener(deleteFanOutTweetListenerAdapter(),
            new PatternTopic("TWEET_TO_BE_DELETED"));

        return container;
    }
}
