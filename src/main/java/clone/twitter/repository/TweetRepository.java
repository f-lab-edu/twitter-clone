package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {

    static final int TWEET_LOAD_LIMIT = 5;

    List<Tweet> findInitialTimelinePageTweets(String userid);

    List<Tweet> findNextTimelinePageTweets(String userid, LocalDateTime createdAt);

    Optional<Tweet> findById(String id);

    Tweet save(Tweet tweet);

    int deleteById(String id);
}
