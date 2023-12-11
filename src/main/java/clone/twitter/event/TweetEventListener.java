package clone.twitter.event;

import clone.twitter.domain.Tweet;
import clone.twitter.event.dto.FanOutMessageDto;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TweetEventListener {

    private final RedisTemplate<String, Object> objectFanOutRedisTemplate;

    public void handleFanOutTweetMessage(FanOutMessageDto fanOutMessageDto) {

        Tweet tweet = fanOutMessageDto.getTweet();
        List<String> followerIds = fanOutMessageDto.getFollowerIds();

        try {
            // Redis Pipelining 처리
            objectFanOutRedisTemplate.execute((RedisCallback<Object>) connection -> {

                followerIds.forEach(followerId -> {

                    // userId(followerId)를 키로써, SortedSet 자료구조를 값으로써 작업할 것임을 정의
                    BoundZSetOperations<String, Object> objectZSetOperations
                        = objectFanOutRedisTemplate.boundZSetOps(followerId);

                    // 트윗의 createdAt 필드값을 Redis의 날짜 표현형식인 Double로 변환
                    double timestampDouble = tweet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);

                    // Redis에서 userId를 키로, 타임라인 트윗목록을 값(createdAt 필드를 스코어로 하는
                    // SortedSet)으로 하여, 자신을 팔로우하는 유저별로 순회하며 트윗 쓰기 작업
                    objectZSetOperations.add(tweet, timestampDouble);
                });

                return null;
            });
        } catch (Exception e) {
            log.error("메시지 처리 실패: handleFanOutTweetMessage(), 예외 타입: {}, fan-out 정보: {}",
                e.getMessage(), fanOutMessageDto);
        }
    }

    public void handleDeleteFanOutTweetMessage (FanOutMessageDto fanOutMessageDto){

        Tweet tweet = fanOutMessageDto.getTweet();
        List<String> followerIds = fanOutMessageDto.getFollowerIds();

        try {// Redis Pipelining 처리
            objectFanOutRedisTemplate.execute((RedisCallback<Object>) connection -> {

                // Redis에서 각 userId에 해당하는 키의 값(SortedSet) 중
                // 삭제할 tweet과 일치하는 요소를 유저별로 순회하며 삭제
                followerIds.forEach(followerId -> {
                    objectFanOutRedisTemplate.opsForZSet().remove(followerId, tweet);
                });

                return null;
            });
        } catch (Exception e) {
            log.error("메시지 처리 실패: handleDeleteFanOutTweetMessage(), 예외 타입: {}, fan-out 정보: {}",
                e.getMessage(), fanOutMessageDto);
        }
    }
}
