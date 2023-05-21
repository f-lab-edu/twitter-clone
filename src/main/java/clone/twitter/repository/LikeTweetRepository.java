package clone.twitter.repository;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import java.util.List;

public interface LikeTweetRepository {
    void save(LikeTweet likeTweet);

    void deleteByTweetIdAndUserId(String tweetId, String userId);

    List<User> findUsersByTweetIdOrderByCreatedAtDesc(String tweetId);

    List<User> findUsersByTweetIdAndUserIdOrderByCreatedAtDesc(String tweetId, String userId);
}
