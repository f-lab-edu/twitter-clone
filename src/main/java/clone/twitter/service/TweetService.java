package clone.twitter.service;

import clone.twitter.domain.Tweet;
import clone.twitter.repository.TweetRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TweetService {
    @Autowired
    private final TweetRepository tweetRepository;

    public Tweet postTweet(Tweet tweet) {
        return tweetRepository.save(new Tweet(tweet.getText(), tweet.getUserId()));
    }

    public List<Tweet> getInitialTweets(String userId) {
        return tweetRepository.findInitialTimelinePageTweets(userId);
    }

    public List<Tweet> getNextTweets(String userId, LocalDateTime createdAtOfTweet) {
        return tweetRepository.findNextTimelinePageTweets(userId, createdAtOfTweet);
    }

    public Optional<Tweet> getTweet(String tweetId) {
        return tweetRepository.findById(tweetId);
    }

    public void deleteTweet(String tweetId) {
        tweetRepository.deleteById(tweetId);
    }
}
