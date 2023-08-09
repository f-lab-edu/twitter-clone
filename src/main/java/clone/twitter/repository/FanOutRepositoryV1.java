package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import clone.twitter.exception.NoSuchEntityException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FanOutRepositoryV1 implements FanOutRepository {

    private final TweetMapper tweetMapper;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public List<String> findCelebFolloweeIds(String redisKey, int startIndex, int endIndex) {

        return stringRedisTemplate.opsForList().range(redisKey, startIndex, endIndex);
    }

    @Override
    public List<Tweet> findListOfTweetsByUserIds(List<String> userIds, int loadLimit) {

        return tweetMapper.findByListOfTweetsByUserIds(userIds, loadLimit);
    }

    @Override
    public Set<Object> findTweetsObjectsOfNonCelebFollowees(
            String userId, int startIndex, int endIndex) {

        // 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 조회
        return objectRedisTemplate.opsForZSet().range(userId, startIndex, endIndex);
    }

    @Override
    public Set<Object> findTweetsObjectsOfNonCelebFollowees(
            String userId, double minScore, double maxScore, int startIndex, int endIndex) {

        // 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 추가 조회
        return objectRedisTemplate.opsForZSet().rangeByScore(
                userId, minScore, maxScore, startIndex, endIndex);
    }

    @Override
    public void operateFanOut(String userId, Tweet tweet) {

        if (!userMapper.checkIfCelebrity(userId)) {
            List<String> followerIds = followMapper.findFollowerIdsByFolloweeId(userId);

            followerIds.forEach(followerId -> {
                BoundZSetOperations<String, Object> objectZSetOperations
                        = objectRedisTemplate.boundZSetOps(followerId);

                double timestampDouble = tweet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);

                objectZSetOperations.add(tweet, timestampDouble);
            });
        }
    }

    @Override
    public void operateDeleteFanOut(String tweetId) {

        Optional<Tweet> optionalTweet = tweetMapper.findById(tweetId);

        if (optionalTweet.isEmpty()) {
            throw new NoSuchEntityException("해당 트윗이 존재하지 않습니다.");
        }

        Tweet tweet = optionalTweet.get();

        if (!userMapper.checkIfCelebrity(tweet.getUserId())) {
            List<String> followerIds = followMapper.findFollowerIdsByFolloweeId(tweet.getUserId());

            followerIds.forEach(followerId -> {
                objectRedisTemplate.opsForZSet().remove(tweet.getUserId(), tweet);
            });
        }
    }
}
