package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {
    /**
     * pagination 시 한 번에 로드되는 트윗의 개수을 지정합니다.
     */
    static final int TWEET_LOAD_LIMIT = 5;

    List<Tweet> findInitialTimelinePageTweets(String userid);

    List<Tweet> findNextTimelinePageTweets(String userid, LocalDateTime createdAt);

    Optional<Tweet> findById(String id);

    Tweet save(Tweet tweet);

    void deleteById(String id);
}
