package clone.twitter.service;

import clone.twitter.domain.Tweet;
import clone.twitter.repository.TweetRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TweetService {
    private final TweetRepository tweetRepository;

    public List<Tweet> getInitialTweets(String userId) {
        return tweetRepository.findInitialTimelinePageTweets(userId);
    }

    public List<Tweet> getNextTweets(String userId, LocalDateTime createdAtOfTweet) {
        return tweetRepository.findNextTimelinePageTweets(userId, createdAtOfTweet);
    }

    public Optional<Tweet> getTweet(String tweetId) {
        return tweetRepository.findById(tweetId);
    }

    public Tweet postTweet(Tweet tweet) {
        return tweetRepository.save(tweet);
    }

    public void deleteTweet(String tweetId) {
        tweetRepository.deleteById(tweetId);
    }
}
