package clone.twitter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisSessionConfig {

    @Value("${spring.redis.session.host}")
    private String redisSessionHost;

    @Value("${spring.redis.session.port}")
    private int redisSessionPort;

    @Value("${spring.redis.session.password}")
    private String redisSessionPassword;

    // spring-session-data-redis 의존성 추가시 해당 라이브러리가 "redisConnectionFactory"라는
    // 이름으로 빈을 자동 등록하게 됩니다. 여기에서 다른 redis connection factory들과 이름 혼동이
    // 올 수 있기에 명시적으로 "redisSessionConnectionFactory"라는 빈 이름을 추가등록하였습니다.
    @Qualifier(value = "redisSessionConnectionFactory")
    @Bean({"redisConnectionFactory", "redisSessionConnectionFactory"})
    public RedisConnectionFactory redisSessionConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setHostName(redisSessionHost);
        redisStandaloneConfiguration.setPort(redisSessionPort);
        redisStandaloneConfiguration.setPassword(redisSessionPassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean({"redisTemplate", "sessionStringRedisTemplate"})
    RedisTemplate<String, String> redisTemplate(
        @Qualifier(value = "redisSessionConnectionFactory")
        RedisConnectionFactory redisSessionConnectionFactory) {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisSessionConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
