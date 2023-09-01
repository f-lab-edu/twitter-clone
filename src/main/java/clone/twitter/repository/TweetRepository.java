package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {

    List<Tweet> findInitialTimelinePageTweets(String userid);

    List<Tweet> findNextTimelinePageTweets(String userid, LocalDateTime createdAt);

    Optional<Tweet> findById(Tweet tweet);

    Tweet save(Tweet tweet);

    int deleteById(Tweet tweet);

    void saveCache(Tweet tweet);

    Optional<Tweet> findCache(Tweet tweet);

    void deleteCache(Tweet tweet);
}
