package clone.twitter.config;

import clone.twitter.event.TweetEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class EventConfig {

    private final RedisConnectionFactory redisFanOutConnectionFactory;

    private final TweetEventListener tweetEventListener;

    @Bean
    public MessageListenerAdapter fanOutTweetListenerAdapter() {
        MessageListenerAdapter adapter = new MessageListenerAdapter(
            tweetEventListener, "handleFanOutTweetMessage");

        adapter.setSerializer(new GenericJackson2JsonRedisSerializer());

        return adapter;
    }

    @Bean
    public MessageListenerAdapter deleteFanOutTweetListenerAdapter() {
        MessageListenerAdapter adapter = new MessageListenerAdapter(tweetEventListener,
            "handleDeleteFanOutTweetMessage");

        adapter.setSerializer(new GenericJackson2JsonRedisSerializer());

        return adapter;

    }

    // message listener container(+connection factory)
    @Bean
    public RedisMessageListenerContainer container(
        MessageListenerAdapter fanOutTweetListenerAdapter,
        MessageListenerAdapter deleteFanOutTweetListenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(redisFanOutConnectionFactory);
        container.addMessageListener(fanOutTweetListenerAdapter, new PatternTopic("NEW_TWEET"));
        container.addMessageListener(deleteFanOutTweetListenerAdapter,
            new PatternTopic("TWEET_TO_BE_DELETED"));

        return container;
    }
}
