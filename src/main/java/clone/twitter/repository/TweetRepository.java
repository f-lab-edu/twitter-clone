package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {

    List<Tweet> findInitialTimelinePageTweets(String userid);

    List<Tweet> findNextTimelinePageTweets(String userid, LocalDateTime createdAt);

    Optional<Tweet> findById(String tweetId);

    Tweet save(Tweet tweet);

    int deleteById(String tweetId);
}
