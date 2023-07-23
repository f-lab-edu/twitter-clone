package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TweetRepositoryV1 implements TweetRepository {

    private final TweetMapper tweetMapper;

    public List<Tweet> findInitialTimelinePageTweets(String userid) {
        return tweetMapper.findInitialTimelinePageTweets(userid, TWEET_LOAD_LIMIT);
    }

    public List<Tweet> findNextTimelinePageTweets(String userid, LocalDateTime createdAt) {
        return tweetMapper.findNextTimelinePageTweets(userid, createdAt, TWEET_LOAD_LIMIT);
    }

    @Override
    public Optional<Tweet> findById(String id) {
        return tweetMapper.findById(id);
    }

    @Override
    public Tweet save(Tweet tweet) {
        tweetMapper.save(tweet);
        return tweet;
    }

    @Override
    public int deleteById(String id) {
        return tweetMapper.deleteById(id);
    }
}
