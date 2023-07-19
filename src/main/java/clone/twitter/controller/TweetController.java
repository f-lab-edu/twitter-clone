package clone.twitter.controller;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_BAD_REQUEST;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_OK;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.dto.request.TweetLoadRequestDto;
import clone.twitter.service.TweetService;
import clone.twitter.util.HttpResponseEntities;
import clone.twitter.util.TweetValidator;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

    @GetMapping("/timeline")
    public ResponseEntity<List<Tweet>> getInitialTweets(@RequestParam String userId) {
        List<Tweet> initialTweets = tweetService.getInitialTweets(userId);

        return ResponseEntity.ok(initialTweets);
    }

    @GetMapping("/timeline/next")
    public ResponseEntity<List<Tweet>> getNextTweets(@RequestBody @Valid TweetLoadRequestDto tweetLoadRequestDto) {
        List<Tweet> nextTweets = tweetService.getNextTweets(tweetLoadRequestDto.getUserIdOfViewer(), tweetLoadRequestDto.getCreatedAtOfLastViewedTweet());

        return ResponseEntity.ok(nextTweets);
    }

    @GetMapping("/{tweetId}")
    public ResponseEntity<?> getTweet(@PathVariable String tweetId) {
        Optional<Tweet> optionalTweet = tweetService.getTweet(tweetId);

        if (optionalTweet.isEmpty()) {
            return HttpResponseEntities.RESPONSE_NOT_FOUND;
        }

        Tweet tweet = optionalTweet.get();

        return ResponseEntity.ok(tweet);
    }

    @PostMapping
    public ResponseEntity<?> composeTweet(@RequestBody @Valid TweetComposeRequestDto tweetComposeRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            // return badRequest(errors); // 이후 적용 예정.
            return RESPONSE_BAD_REQUEST;
        }

        tweetValidator.validate(tweetComposeRequestDto, errors);

        if (errors.hasErrors()) {
            // return badRequest(errors); // 이후 적용 예정.
            return RESPONSE_BAD_REQUEST;
        }

        Tweet tweet = tweetService.composeTweet(tweetComposeRequestDto);

        URI createdUri = WebMvcLinkBuilder.
            linkTo(TweetController.class)
            .slash(tweet.getId())
            .toUri();

        return ResponseEntity.created(createdUri).body(tweet);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<Void> deleteTweet(@PathVariable String tweetId) {
        boolean deleted = tweetService.deleteTweet(tweetId);

        return RESPONSE_OK;
    }
}
