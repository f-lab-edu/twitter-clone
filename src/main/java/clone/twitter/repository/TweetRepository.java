package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {

    List<Tweet> findAllByOrderByCreatedAtDesc();

    List<Tweet> findAllByUserIdByOrderByCreatedAtDesc(Long userId);

    Optional<Tweet> findById(Long id);

    Tweet save(Tweet tweet);

    void deleteById(Long id);
}
