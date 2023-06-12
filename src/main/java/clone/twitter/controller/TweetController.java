package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.common.ErrorEntityModel;
import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetLoadRequestDto;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.service.TweetService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
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

@RequiredArgsConstructor
@RequestMapping(value = "/tweets", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class TweetController {
    @Autowired
    private final TweetService tweetService;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final TweetValidator tweetValidator;

    /**
     * 타임라인의 최초 트윗 목록 조회 요청을 처리합니다(이후 API 다음 버전에서 아래의 getNextTweets() 메서드와 합치도록 리팩토링 예정).
     */
    @GetMapping("/timeline")
//    public ResponseEntity getInitialTweets(@RequestBody String userId) {
    public ResponseEntity<CollectionModel<TweetEntityModel>> getInitialTweets(@RequestParam String userId) {
        List<Tweet> initialTweets = tweetService.getInitialTweets(userId);

        // For each Tweet, wrap it in an EntityModel and add a self-link
        List<TweetEntityModel> tweetModels = initialTweets.stream()
            .map(tweet -> {
                TweetEntityModel tweetEntityModel = new TweetEntityModel(tweet);

                tweetEntityModel.add(Link.of("/docs/index.html#get-tweet").withRel("profile"));

                return tweetEntityModel;
            })
//            .map(tweet -> EntityModel.of(tweet, linkTo(methodOn(TweetController.class).getTweet(tweet.getId())).withSelfRel()))
            .collect(Collectors.toList());

        // Create a CollectionModel to wrap the list and add a self-link
        CollectionModel<TweetEntityModel> collectionModel = CollectionModel.of(tweetModels, linkTo(methodOn(TweetController.class).getInitialTweets(userId)).withSelfRel());

        collectionModel.add(Link.of("/docs/index.html#get-initial-tweets").withRel("profile"));

        return ResponseEntity.ok(collectionModel);
    }
//    public List<Tweet> getInitialTweets(@RequestBody String userId) {
//        return tweetService.getInitialTweets(userId);
//    }


    /**
     * 이전 트윗목록의 끝까지 모두 조회했을 시 트윗목록을 추가 조회 요청을 처리합니다(이후 API 다음 버전에서 위의 getInitialTweets() 메서드와 합치도록 리팩토링 예정).
     */
    @GetMapping("/timeline/next")
    public ResponseEntity<CollectionModel<TweetEntityModel>> getNextTweets(@RequestBody @Valid TweetLoadRequestDto tweetLoadRequestDto) {
//    public List<Tweet> getNextTweets(@RequestBody @Valid TweetLoadRequestDto tweetLoadRequestDto) {
        List<Tweet> nextTweets = tweetService.getNextTweets(tweetLoadRequestDto.getUserIdOfViewer(), tweetLoadRequestDto.getCreatedAtOfLastViewedTweet());

        // For each Tweet, wrap it in an EntityModel and add a self-link
        List<TweetEntityModel> tweetModels = nextTweets.stream()
            .map(tweet -> {
                TweetEntityModel tweetEntityModel = new TweetEntityModel(tweet);

                tweetEntityModel.add(Link.of("/docs/index.html#get-tweet").withRel("profile"));

                return tweetEntityModel;
            })
//            .map(tweet -> EntityModel.of(tweet, linkTo(methodOn(TweetController.class).getTweet(tweet.getId())).withSelfRel()))
            .collect(Collectors.toList());

        // Create a CollectionModel to wrap the list and add a self-link
        CollectionModel<TweetEntityModel> collectionModel = CollectionModel.of(tweetModels, linkTo(methodOn(TweetController.class).getNextTweets(tweetLoadRequestDto)).withSelfRel());

        collectionModel.add(Link.of("/docs/index.html#get-next-tweets").withRel("profile"));

        return ResponseEntity.ok(collectionModel);
    }
//    public List<Tweet> getNextTweets(@RequestBody @Valid TweetLoadRequestDto tweetLoadRequestDto) {
//        return tweetService.getNextTweets(tweetLoadRequestDto.getUserId(), tweetLoadRequestDto.getCreatedAtOfLastTweet());
//    }
//

    /**
     * 트윗의 상세내용 조회 요청을 처리합니다.
     */
    @GetMapping("/{tweetId}")
    public ResponseEntity<TweetEntityModel> getTweet(@PathVariable String tweetId) {
//    public Optional<Tweet> getTweet(@PathVariable String tweetId) {
        Optional<Tweet> optionalTweet = tweetService.getTweet(tweetId);

        if (optionalTweet.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tweet tweet = optionalTweet.get();

        TweetEntityModel tweetEntityModel = new TweetEntityModel(tweet);

        tweetEntityModel.add(Link.of("/docs/index.html#get-tweet").withRel("profile"));

        return ResponseEntity.ok(tweetEntityModel);
//        return tweetService.getTweet(tweetId)
//            .map(tweet -> ResponseEntity.ok().body(tweet))
//            .orElseGet(() -> ResponseEntity.notFound().build());
//        return tweetService.getTweet(tweetId);
    }

    /**
     * 트윗 포스팅 요청을 처리합니다.
     */
    @PostMapping
    public ResponseEntity<?> composeTweet(@RequestBody @Valid TweetComposeRequestDto tweetComposeDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

//        tweetValidator.validate(tweetComposeDto, errors);
//
//        if (errors.hasErrors()) {
//            return badRequest(errors);
//        }

        Tweet tweet = modelMapper.map(tweetComposeDto, Tweet.class);
        ////ModelMapper를 사용하지 않을 시 아래와 같이 구현 가능하며, 해당 경우 성능상 이점 외에 tweetComposeDto의 필드 설정을 통해 원하지 않는 필드와 값이 추가로 들어오는 걸 원천적으로 막을 수 있는 이점이 있다.
        //Tweet tweet = Tweet.builder()
        //    .text(tweetRequestDto.getText())
        //    .userId(tweetRequestDto.getUserId())
        //    .build();

        Tweet newTweet = tweetService.composeTweet(tweet);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(TweetController.class).slash(newTweet.getId());

        URI createdUri = selfLinkBuilder.toUri();

        TweetEntityModel tweetEntityModel = new TweetEntityModel(newTweet);

        tweetEntityModel.add(linkTo(TweetController.class).slash("timeline").withRel("timeline-tweets"));

        tweetEntityModel.add(Link.of("/docs/index.html#resources-tweets-compose").withRel("profile"));

        return ResponseEntity.created(createdUri).body(tweetEntityModel);
    }

    private static ResponseEntity<ErrorEntityModel> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorEntityModel(errors));
    }

//    /**
//     * 트윗 삭제 요청을 처리합니다.
//     */
//    @DeleteMapping("/{tweetId}")
//    public void deleteTweet(@PathVariable String tweetId) {
//        tweetService.deleteTweet(tweetId);
//    }
}
