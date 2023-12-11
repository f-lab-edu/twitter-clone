package clone.twitter.config;

import static clone.twitter.util.CacheConstant.CACHE_DURATION_IN_HOUR;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /* 테스트 v.3.1: redis session storage 제거

    @Value("${spring.redis.session.host}")
    private String redisSessionHost;

    @Value("${spring.redis.session.port}")
    private int redisSessionPort;

    @Value("${spring.redis.session.password}")
    private String redisSessionPassword;
     */

//    @Value("${spring.redis.fan-out.host}")
    private String redisFanOutHost = "127.0.0.1";

//    @Value("${spring.redis.fan-out.port}")
    private int redisFanOutPort = 6379;

//    @Value("${spring.redis.fan-out.password}")
    private String redisFanOutPassword = "";

    /*
    // 테스트 v.3.1: redis session storage 제거
    //
    // spring-session-data-redis 의존성 추가시 해당 라이브러리가 "redisConnectionFactory"라는
    // 이름으로 빈을 자동 등록하게 됩니다. 여기에서 다른 redis connection factory들과 이름 혼동이
    // 올 수 있기에 명시적으로 "redisSessionConnectionFactory"라는 빈 이름을 추가등록하였습니다.
    @Bean({"redisConnectionFactory", "redisSessionConnectionFactory"})
    public RedisConnectionFactory redisSessionConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setHostName(redisSessionHost);
        redisStandaloneConfiguration.setPort(redisSessionPort);
        redisStandaloneConfiguration.setPassword(redisSessionPassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
     */

    @Bean
    public RedisConnectionFactory redisFanOutConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setHostName(redisFanOutHost);
        redisStandaloneConfiguration.setPort(redisFanOutPort);
        redisStandaloneConfiguration.setPassword(redisFanOutPassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> objectFanOutRedisTemplate(
        RedisConnectionFactory redisFanOutConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // connection factory를 인수로 받아 템플릿에 설정하던 형식에서,
        // 빈 등록시 설정에 의해 고정된 connection factory를 사용하는 형식으로 변경
        redisTemplate.setConnectionFactory(redisFanOutConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, String> stringFanOutRedisTemplate(
        RedisConnectionFactory redisFanOutConnectionFactory) {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // connection factory를 인수로 받아 템플릿에 설정하던 형식에서,
        // 빈 등록시 설정에 의해 고정된 connection factory를 사용하는 형식으로 변경
        redisTemplate.setConnectionFactory(redisFanOutConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    /*
    // redis cache 사용 보류
    //
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> redisCacheConfigMap = new HashMap<>();

        redisCacheConfigMap.put("post", defaultConfig.entryTtl(Duration.ofHours(CACHE_DURATION_IN_HOUR)));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(redisCacheConfigMap)
                .build();
    }
     */
}
