package clone.twitter.repository;

import static clone.twitter.util.EventConstant.deleteFanOutTweetEventChannel;
import static clone.twitter.util.EventConstant.fanOutTweetEventChannel;

import clone.twitter.domain.Tweet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FanOutPublisherRepository implements FanOutRepository {

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

        HashMap<String, Object> fanOutData = createFanOutMessage(followerIds, tweet);

        // Fan-out Tweet Message 발행
        objectFanOutRedisTemplate.convertAndSend(fanOutTweetEventChannel, fanOutData);
    }

    // 비동기 콜백 예외 처리: FanOutAsyncExceptionHandler(message broker 구현시 활용 가능)
    @Async
    @Override
    public void operateDeleteFanOut(List<String> followerIds, Tweet tweet) {

        HashMap<String, Object> fanOutData = createFanOutMessage(followerIds, tweet);

        // Delete Fan-out Tweet Message 발행
        objectFanOutRedisTemplate.convertAndSend(deleteFanOutTweetEventChannel, fanOutData);
    }

    private static HashMap<String, Object> createFanOutMessage(List<String> followerIds,
        Tweet tweet) {

        HashMap<String, Object> fanOutData = new HashMap<>();

        fanOutData.put("tweet", tweet);
        fanOutData.put("followerIds", followerIds);

        return fanOutData;
    }
}
