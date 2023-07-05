package clone.twitter.repository;

import clone.twitter.domain.Tweet;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

/**
 * Prototype v.1 for tweet APIs.
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class TweetRepositoryV1 implements TweetRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final TweetMapper tweetMapper;

    /**
     * pagination 시 한 번에 로드되는 트윗의 개수을 지정합니다.
     */
    private static final int TWEET_LOAD_LIMIT = 5;

    /**
     * 타임라인에 처음 접근했을 시 고정된 수의 트윗을 불러옵니다.
     * @param userid 타임라인을 조회하려는 유저의 아이디
     * @return 유저 자신 및 자신이 팔로우하는 유저가 생성한 특정 수의 트윗 목록. 생성일자 기준 최근부터 내림차순으로 정렬되어 있습니다.
     */
    public List<Tweet> findInitialTimelinePageTweets(String userid) {
        return tweetMapper.findInitialTimelinePageTweets(userid, TWEET_LOAD_LIMIT);
    }

    /**
     * 타임라인을 스크롤 다운하여 이전에 불러온 트윗 목록의 마지막 트윗에 도달했을 때, 고정된 수의 트윗을 추가로 불러옵니다.
     * @param userid 타임라인을 조회중인 유저의 아이디
     * @param createdAt 이전에 불러온 트윗 목록 중 마지막 트윗의 생성일자
     * @return 유저 자신 및 자신이 팔로우하는 유저가 생성한 특정 수의 트윗 목록. 이전에 불러온 마지막 트윗 생성일자 이후부터 생성일자 기준 내림차순으로 정렬되어 있습니다.
     */
    public List<Tweet> findNextTimelinePageTweets(String userid, LocalDateTime createdAt) {
        return tweetMapper.findNextTimelinePageTweets(userid, createdAt, TWEET_LOAD_LIMIT);
    }

    /**
     * this method is primarily for general internal operations.
     *
     * @param id primary key of a specific tweet
     * @return a specific tweet matching the id
     */
    @Override
    @Cacheable(value = "tweets", key = "#id")
    public Optional<Tweet> findById(String id) {
        return tweetMapper.findById(id);
    }

    /**
     * for post api request
     * @param tweet new tweet requested by the user
     * @return complete information of the tweet posted by the user
     */
    @Override
    @Cacheable(value = "tweets", key = "#tweet.id")
    public Tweet save(Tweet tweet) {
        tweetMapper.save(tweet);
        saveCache(tweet);
        return tweet;
    }

    /**
     * for delete request of a tweet
     * @param
     */
    public void deleteById(String id) {
        tweetMapper.deleteById(id);
    }

    /**
     * tweet을 생성 시 redis에 sorted set으로 저장을 합니다.
     */
    @Override
    @Cacheable(value = "tweets", key = "#tweet.id")
    public void saveCache(Tweet tweet) {

        String userId = tweet.getUserId();
        BoundZSetOperations<String, Object> stringObjectZSetOperations = redisTemplate.boundZSetOps(userId);
        double timestampDouble = tweet.getCreatedAt().toEpochSecond(ZoneOffset.UTC);

        stringObjectZSetOperations.add(tweet, timestampDouble);
    }

    /**
     *
     * @param userId
     * @param tweetId
     * @return
     * redis key-value로 userId, tweetId를 파라미터를 주면 해당 트윗 객체를 반환해줍니다.
     */
    @Override
    public Tweet findCache(String userId, String tweetId) {
        Set<Object> tweets = redisTemplate.opsForZSet().range(userId, 0, -1);

        for (Object tweet : tweets) {
            Tweet tweetObject = (Tweet) tweet;

            if(tweetId.equals(tweetObject.getId())){
                return tweetObject;
            }
        }
        return null;
    }

    /**
     * for delete request of a tweet in redis
     *
     */
    public void deleteCache(Tweet tweet){
        redisTemplate.opsForZSet().remove(tweet.getUserId(), tweet);
    }
}
