package clone.twitter.service;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.repository.TweetRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class TweetService {
    @Autowired
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

    public Tweet composeTweet(TweetComposeRequestDto tweetComposeRequestDto) {
        return tweetRepository.save(new Tweet(tweetComposeRequestDto.getText(), tweetComposeRequestDto.getUserId()));
    }

    public boolean deleteTweet(String tweetId) {
        int rowsAffected = tweetRepository.deleteById(tweetId);

        return rowsAffected > 0;
    }
}
