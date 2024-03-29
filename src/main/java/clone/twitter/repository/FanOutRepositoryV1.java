package clone.twitter.repository;

import static clone.twitter.util.EventConstant.deleteFanOutTweetEventChannel;
import static clone.twitter.util.EventConstant.fanOutTweetEventChannel;

import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.exception.NoSuchEntityException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FanOutRepositoryV1 implements FanOutRepository {

    private final TweetMapper tweetMapper;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

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

    // 콜백 예외 처리: FanOutAsyncExceptionHandler -> messaging 적용시 대체 예정
    @Async
    @Override
    public void operateFanOut(String userId, Tweet tweet) {

        Optional<User> optionalUser = userMapper.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new NoSuchEntityException("해당 유저가 존재하지 않습니다.");
        }

        // 자신 계정이 셀럽 계정에 해당하는지 확인
        if (!optionalUser.get().isCelebrity()) {
            // (본인 계정이 셀럽 계정에 해당하지 않는 경우에 한해)본인을 팔로우하는 userId(followeeId) 목록 조회
            List<String> followerIds = followMapper.findFollowerIdsByFolloweeId(userId);

            HashMap<String, Object> fanOutData = new HashMap<>();
            fanOutData.put("tweet", tweet);
            fanOutData.put("followerIds", followerIds);

            // Fan-out Tweet Message 발행
            objectFanOutRedisTemplate.convertAndSend(fanOutTweetEventChannel, fanOutData);
        }
    }

    //  콜백 예외 처리: FanOutAsyncExceptionHandler -> messaging 적용시 대체 예정
    @Async
    @Override
    public void operateDeleteFanOut(String tweetId) {

        Optional<Tweet> optionalTweet = tweetMapper.findById(tweetId);

        if (optionalTweet.isEmpty()) {
            throw new NoSuchEntityException("해당 트윗이 존재하지 않습니다.");
        }

        Tweet tweet = optionalTweet.get();

        Optional<User> optionalUser = userMapper.findById(tweet.getUserId());

        if (optionalUser.isEmpty()) {
            throw new NoSuchEntityException("해당 유저가 존재하지 않습니다.");
        }

        // 자신 계정이 셀럽 계정에 해당하는지 확인
        if (!optionalUser.get().isCelebrity()) {
            // (본인 계정이 셀럽 계정에 해당하지 않는 경우에 한해)본인을 팔로우하는 userId(followeeId) 목록 조회
            List<String> followerIds = followMapper.findFollowerIdsByFolloweeId(tweet.getUserId());

            HashMap<String, Object> fanOutData = new HashMap<>();
            fanOutData.put("tweet", tweet);
            fanOutData.put("followerIds", followerIds);

            // Delete Fan-out Tweet Message 발행
            objectFanOutRedisTemplate.convertAndSend(deleteFanOutTweetEventChannel, fanOutData);
        }
    }
}
