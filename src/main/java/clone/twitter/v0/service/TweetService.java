package clone.twitter.v0.service;

import clone.twitter.v0.domain.Tweet;
import clone.twitter.v0.dto.request.TweetRequestDto;
import clone.twitter.v0.dto.response.ResponseDto;
import clone.twitter.v0.dto.response.TweetResponseDto;
import clone.twitter.v0.repository.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TweetService {

    private final TweetRepository tweetRepository;

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllTweets() {
        return ResponseDto.success(tweetRepository.findAllByOrderByCreatedAtDesc());
    }


    @Transactional(readOnly = true)
    public ResponseDto<?> getAllTweetsByMemberId(String memberId) {
        return ResponseDto.success(tweetRepository.findAllByMemberIdByOrderByCreatedAtDesc(memberId));
    }


    @Transactional(readOnly = true)
    public ResponseDto<?> getTweet(Long tweetId) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);

        if (optionalTweet.isEmpty()) {
            return ResponseDto.fail("TWEET_ID_NOT_FOUND", "tweet does not exist");
        }
        return ResponseDto.success(optionalTweet.get());
    }


    @Transactional
    public ResponseDto<?> createTweet(TweetRequestDto tweetRequestDto) {
        Tweet tweet = Tweet.builder()
                .tweetId(tweetRequestDto.getTweetId())
                .memberId(tweetRequestDto.getMemberId())
                .content(tweetRequestDto.getContent())
                .build();
        tweetRepository.save(tweet);
        return ResponseDto.success(TweetResponseDto.builder()
                .tweetId(tweet.getTweetId())
                .memberId(tweet.getMemberId())
                .content(tweet.getContent())
                .createdAt(tweet.getCreatedAt())
                .build()
        );
    }


    @Transactional
    public ResponseDto<?> deleteTweet(Long tweetId) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);

        if (optionalTweet.isEmpty()) {
            return ResponseDto.fail("TWEET_ID_NOT_FOUND", "tweet does not exits");
        }
        tweetRepository.deleteById(tweetId);
        return ResponseDto.success(tweetId);
    }
}
