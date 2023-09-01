package clone.twitter.service;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TweetService {

    public List<Tweet> getInitialTweets(String userId);

    public List<Tweet> getMoreTweets(String userId, LocalDateTime createdAtOfTweet);

    public Optional<Tweet> getTweet(String tweetId);

    public Tweet composeTweet(String userId, TweetComposeRequestDto tweetComposeRequestDto);

    public void deleteTweet(String tweetId);
}
