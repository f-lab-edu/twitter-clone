package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * Prototype v.1 for tweet APIs.
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class TweetRepositoryV1 implements TweetRepository {
    private final TweetMapper tweetMapper;

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
     * @param id primary key of a specific tweet
     * @return a specific tweet matching the id
     */
    @Override
    public Optional<Tweet> findById(String id) {
        return tweetMapper.findById(id);
    }

    /**
     * for post api request
     * @param tweet new tweet requested by the user
     * @return complete information of the tweet posted by the user
     */
    @Override
    public Tweet save(Tweet tweet) {
        tweetMapper.save(tweet);
        return tweet;
    }

    /**
     * for delete request of a tweet
     * @param id primary key of a specific tweet
     */
    @Override
    public int deleteById(String id) {
        return tweetMapper.deleteById(id);
    }
}
