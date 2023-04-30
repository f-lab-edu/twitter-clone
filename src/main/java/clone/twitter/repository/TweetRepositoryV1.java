package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * Prototype v.1
 * for tweet APIs
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class TweetRepositoryV1 implements TweetRepository {
    private final TweetMapper tweetMapper;

    /**
     * for tweets on timeline board
     * @return all tweets in descending order
     */
    @Override
    public List<Tweet> findAllByOrderByCreatedAtDesc() {
        return tweetMapper.findAllByOrderByCreatedAtDesc();
    }

    /**
     * for tweets on user's profile page
     * @param userId primary key of specific user account
     * @return all tweets of a user in descending order
     */
    @Override
    public List<Tweet> findAllByUserIdByOrderByCreatedAtDesc(Long userId) {
        return tweetMapper.findAllByUserIdByOrderByCreatedAtDesc(userId);
    }

    /**
     * this method is primarily for general internal operations.
     * @param id primary key of a specific tweet
     * @return a specific tweet matching the id
     */
    @Override
    public Optional<Tweet> findById(Long id) {
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
    public void deleteById(Long id) {
        tweetMapper.deleteById(id);
    }

    /**
     * for internal test only
     */
    public void clear() {
        tweetMapper.clear();
    }
}
