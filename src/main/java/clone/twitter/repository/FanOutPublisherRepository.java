package clone.twitter.repository;

import static clone.twitter.util.EventConstant.deleteFanOutTweetEventChannel;
import static clone.twitter.util.EventConstant.fanOutTweetEventChannel;

import clone.twitter.domain.Tweet;
import clone.twitter.event.dto.FanOutMessageDto;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Slf4j
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

    // 비동기 콜백 예외 처리: FanOutAsyncExceptionHandler(발생시 Message 발행 실패 정보 로깅)
    @Async
    @Override
    public void operateFanOut(List<String> followerIds, Tweet tweet) {

        FanOutMessageDto fanOutMessageDto = createFanOutMessage(followerIds, tweet);

        // Fan-out Tweet Message 발행
        objectFanOutRedisTemplate.convertAndSend(fanOutTweetEventChannel, fanOutMessageDto);
    }

    // 비동기 콜백 예외 처리: FanOutAsyncExceptionHandler(발생시 Message 발행 실패 정보 로깅)
    @Async
    @Override
    public void operateDeleteFanOut(List<String> followerIds, Tweet tweet) {

        FanOutMessageDto fanOutMessageDto = createFanOutMessage(followerIds, tweet);

        // Delete Fan-out Tweet Message 발행
        objectFanOutRedisTemplate.convertAndSend(deleteFanOutTweetEventChannel, fanOutMessageDto);
    }

    private static FanOutMessageDto createFanOutMessage(List<String> followerIds, Tweet tweet) {

        return FanOutMessageDto.builder()
            .tweet(tweet)
            .followerIds(followerIds)
            .build();
    }
}
