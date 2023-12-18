package clone.twitter.config;

import static clone.twitter.util.CacheConstant.CACHE_DURATION_IN_HOUR;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Value("${spring.redis.fan-out.host}")
    private String redisFanOutHost;

    @Value("${spring.redis.fan-out.port}")
    private int redisFanOutPort;

    @Value("${spring.redis.fan-out.password}")
    private String redisFanOutPassword;

    @Bean
    @Qualifier(value = "redisFanOutConnectionFactory")
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
        @Qualifier(value = "redisFanOutConnectionFactory")
        RedisConnectionFactory redisFanOutConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisFanOutConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, String> stringFanOutRedisTemplate(
        @Qualifier(value = "redisFanOutConnectionFactory")
        RedisConnectionFactory redisFanOutConnectionFactory) {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisFanOutConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager cacheManager(
        @Qualifier(value = "redisFanOutConnectionFactory")
        RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration defaultConfig
            = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(
                            new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> redisCacheConfigMap = new HashMap<>();

        redisCacheConfigMap.put(
            "post", defaultConfig.entryTtl(Duration.ofHours(CACHE_DURATION_IN_HOUR)));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(redisCacheConfigMap)
                .build();
    }
}
