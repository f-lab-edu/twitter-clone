package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FanOutDefaultRepository implements FanOutRepository {

    private final TweetMapper tweetMapper;

    private final RedisTemplate<String, Object> objectFanOutRedisTemplate;
    private final RedisTemplate<String, String> stringFanOutRedisTemplate;

    @Override
    public List<String> findCelebFolloweeIds(String redisKey, int startIndex, int endIndex) {

        return stringFanOutRedisTemplate.opsForList().range(redisKey, startIndex, endIndex);
    }

    @Override
    public List<Tweet> findListOfTweetsByUserIds(List<String> userIds, int loadLimit) {

        return tweetMapper.findByListOfTweetsByUserIds(userIds, loadLimit);
    }

    @Override
    public Set<Object> findTweetsObjectsOfNonCelebFollowees(
            String userId, int startIndex, int endIndex) {

        // 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 조회
        return objectFanOutRedisTemplate.opsForZSet().range(userId, startIndex, endIndex);
    }

    @Override
    public Set<Object> findTweetsObjectsOfNonCelebFollowees(
            String userId, double minScore, double maxScore, int startIndex, int endIndex) {

        // 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 추가 조회
        return objectFanOutRedisTemplate.opsForZSet().rangeByScore(
                userId, minScore, maxScore, startIndex, endIndex);
    }

    // 비동기 콜백 예외 처리: FanOutAsyncExceptionHandler(message broker 구현시 활용 가능)
    @Async
    @Override
    public void operateFanOut(List<String> followerIds, Tweet tweet) {

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
    }

    // 비동기 콜백 예외 처리: FanOutAsyncExceptionHandler(message broker 구현시 활용 가능)
    @Async
    @Override
    public void operateDeleteFanOut(List<String> followerIds, Tweet tweet) {

        // Redis Pipelining 처리
        objectFanOutRedisTemplate.execute((RedisCallback<Object>) connection -> {

            // Redis에서 각 userId에 해당하는 키의 값(SortedSet) 중
            // 삭제할 tweet과 일치하는 요소를 유저별로 순회하며 삭제
            followerIds.forEach(followerId -> {
                objectFanOutRedisTemplate.opsForZSet().remove(followerId, tweet);
            });

            return null;
        });
    }
}
