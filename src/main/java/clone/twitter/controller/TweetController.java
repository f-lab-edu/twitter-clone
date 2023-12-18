package clone.twitter.controller;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_OK;

import clone.twitter.annotation.AuthenticationCheck;
import clone.twitter.annotation.SignedInUserId;
import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.service.TweetService;
import clone.twitter.util.HttpResponseEntities;
import clone.twitter.util.TweetValidator;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    private final TweetValidator tweetValidator;

    @AuthenticationCheck
    @GetMapping("/timeline")
    public ResponseEntity<List<Tweet>> getInitialTweets(@SignedInUserId String userId) {
        List<Tweet> initialTweets = tweetService.getInitialTweets(userId);

        return ResponseEntity.ok(initialTweets);
    }

    @AuthenticationCheck
    @PostMapping("/timeline/more")
    public ResponseEntity<List<Tweet>> getMoreTweets(@SignedInUserId String userId,
        @RequestParam LocalDateTime createdAtOfTweetLastOnList) {

        List<Tweet> nextTweets = tweetService.getMoreTweets(userId, createdAtOfTweetLastOnList);

        return ResponseEntity.ok(nextTweets);
    }

    @GetMapping("/{tweetId}")
    public ResponseEntity<Tweet> getTweet(@PathVariable String tweetId) {
        Optional<Tweet> optionalTweet = tweetService.getTweet(tweetId);

        if (optionalTweet.isEmpty()) {
            return HttpResponseEntities.notFound();
        }

        Tweet tweetDetails = optionalTweet.get();

        return ResponseEntity.ok(tweet);
    }

    @AuthenticationCheck
    @PostMapping
    public ResponseEntity<Tweet> composeTweet(@SignedInUserId String userId,
        @RequestBody @Valid TweetComposeRequestDto tweetComposeRequestDto, Errors errors) {

        if (errors.hasErrors()) {
            return HttpResponseEntities.badRequest();
        }

        Tweet tweet = tweetService.composeTweet(userId, tweetComposeRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(tweet);
    }

    @AuthenticationCheck
    @DeleteMapping("/{tweetId}")
    public ResponseEntity<Void> deleteTweet(@PathVariable String tweetId) {
        tweetService.deleteTweet(tweetId);

        return RESPONSE_OK;
    }
}
