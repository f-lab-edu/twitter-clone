package clone.twitter.service;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.repository.TweetRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class TweetDefaultService implements TweetService {

    private final TweetRepository tweetRepository;

    @Override
    public List<Tweet> getInitialTweets(String userId) {
        return tweetRepository.findInitialTimelinePageTweets(userId);
    }

    @Override
    public List<Tweet> getMoreTweets(String userId, LocalDateTime createdAtOfTweet) {
        return tweetRepository.findNextTimelinePageTweets(userId, createdAtOfTweet);
    }

    @Override
    public Optional<Tweet> getTweet(String tweetId) {
        return tweetRepository.findById(tweetId);
    }

    @Override
    public Tweet composeTweet(String userId, TweetComposeRequestDto tweetComposeRequestDto) {

        Tweet tweet = Tweet.builder()
            .id(UUID.randomUUID().toString())
            .text(tweetComposeRequestDto.getText())
            .userId(userId)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

        return tweetRepository.save(tweet);
    }

    @Override
    public void deleteTweet(String tweetId) {
        tweetRepository.deleteById(tweetId);
    }
}
