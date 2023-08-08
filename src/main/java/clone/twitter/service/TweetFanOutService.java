package clone.twitter.service;

import static clone.twitter.repository.TweetRepository.TWEET_LOAD_LIMIT;
import static clone.twitter.util.CacheConstant.CELEB_ID_LIST_KEY_PREFIX;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.repository.FollowMapper;
import clone.twitter.repository.TweetMapper;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserMapper;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class TweetFanOutService implements TweetService {

    private final TweetRepository tweetRepository;

    private final TweetMapper tweetMapper;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    private final StringBuffer stringBuffer;

    private static final int INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH = 0;
    private static final int INT_NEGATIVE_ONE_AS_END_INDEX_OF_RANGE_SEARCH = -1;

    @Override
    public List<Tweet> getInitialTweets(String userId) {

        List<Tweet> tweetsOfCelebFollowees = getCelebTweets(userId);

        // (내가 팔로우하는)celeb 외 유저들의 fan-out된 tweet 목록 조회
        Set<Object> tweetsObjectsOfNonCelebFollowees = objectRedisTemplate.opsForZSet().range(
                userId,
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                TWEET_LOAD_LIMIT);

        if (tweetsObjectsOfNonCelebFollowees != null) {

            return mergeAndSortTweetsByCreatedAtDescOrder(
                    tweetsOfCelebFollowees,
                    tweetsObjectsOfNonCelebFollowees);
        }

        return Collections.emptyList();
    }

    @Override
    public List<Tweet> getMoreTweets(String userId, LocalDateTime createdAtOfTweet) {

        // (내가 팔로우하는)celeb의 userId 목록 조회
        List<Tweet> tweetsOfCelebFollowees = getCelebTweets(userId);

        // (내가 팔로우하는)celeb 외 유저들의 fan-out된 tweet 목록 조회
        Set<Object> tweetsObjectsOfNonCelebFollowees = objectRedisTemplate.opsForZSet().rangeByScore(
                userId,
                Double.MIN_VALUE,
                createdAtOfTweet.toEpochSecond(ZoneOffset.UTC),
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                TWEET_LOAD_LIMIT);

        if (tweetsObjectsOfNonCelebFollowees != null) {

            return mergeAndSortTweetsByCreatedAtDescOrder(
                    tweetsOfCelebFollowees,
                    tweetsObjectsOfNonCelebFollowees);
        }

        return Collections.emptyList();
    }

    @Override
    public Optional<Tweet> getTweet(String tweetId) {
        return tweetRepository.findById(tweetId);
    }

    @Override
    public Tweet composeTweet(String userId, TweetComposeRequestDto tweetComposeRequestDto) {

        Tweet tweet = Tweet.builder()
            .id(UUID.randomUUID().toString())
            .text(tweetComposeRequestDto.getText())
            .userId(userId)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

        operateFanOut(userId, tweet);

        return tweetRepository.save(tweet);
    }

    @Override
    public void deleteTweet(String tweetId) {
        tweetRepository.deleteById(tweetId);
    }

    private void operateFanOut(String userId, Tweet tweet) {
        if (!userMapper.isCelebrity(userId)) {
            List<String> followerIds = followMapper.findFollowerIdsByFolloweeId(userId);

            followerIds.forEach(followerId -> {
                BoundZSetOperations<String, Object> stringObjectZSetOperations = objectRedisTemplate
                        .boundZSetOps(followerId);

                double timestampDouble = tweet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);

                stringObjectZSetOperations.add(tweet, timestampDouble);
            });
        }
    }

    private List<Tweet> getCelebTweets(String userId) {
        // (내가 팔로우하는)celeb의 userId 목록 조회
        String myCelebIdsKey = stringBuffer
                .append(CELEB_ID_LIST_KEY_PREFIX)
                .append(userId)
                .toString();

        List<String> myCelebIds = stringRedisTemplate.opsForList().range(
                myCelebIdsKey,
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                INT_NEGATIVE_ONE_AS_END_INDEX_OF_RANGE_SEARCH);

        // (내가 팔로우하는)celeb의 tweet 목록 조회
        return tweetMapper.findByListOfUserIds(myCelebIds, TWEET_LOAD_LIMIT);
    }

    private static List<Tweet> mergeAndSortTweetsByCreatedAtDescOrder(List<Tweet> tweetsObjectsOfCelebFollowees,
            Set<Object> tweetsObjectsOfNonCelebFollowees) {

        List<Tweet> mergedTweets = new ArrayList<>(TWEET_LOAD_LIMIT * 2);

        return mergedTweets = Stream.concat(
                        tweetsObjectsOfCelebFollowees.stream(),
                        tweetsObjectsOfNonCelebFollowees.stream()
                                .filter(obj -> obj instanceof Tweet)
                                .map(obj -> (Tweet) obj))
                .sorted(Comparator.comparing(
                        Tweet::getCreatedAt,
                        Comparator.reverseOrder()))
                .limit(TWEET_LOAD_LIMIT)
                .collect(Collectors.toList());
    }
}
