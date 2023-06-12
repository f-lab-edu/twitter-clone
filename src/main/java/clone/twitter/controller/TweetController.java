package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.common.ErrorEntityModel;
import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.dto.request.TweetLoadRequestDto;
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
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<CollectionModel<TweetEntityModel>> getInitialTweets(@RequestParam String userId) {
        List<Tweet> initialTweets = tweetService.getInitialTweets(userId);

        List<TweetEntityModel> tweetModels = initialTweets.stream()
            .map(tweet -> {
                TweetEntityModel tweetEntityModel = new TweetEntityModel(tweet);

                tweetEntityModel.add(Link.of("/docs/index.html#get-tweet").withRel("profile"));

                return tweetEntityModel;
            })
            .collect(Collectors.toList());

        CollectionModel<TweetEntityModel> collectionModel = CollectionModel.of(tweetModels, linkTo(methodOn(TweetController.class).getInitialTweets(userId)).withSelfRel());

        collectionModel.add(Link.of("/docs/index.html#get-initial-tweets").withRel("profile"));

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * 이전 트윗목록의 끝까지 모두 조회했을 시 트윗목록을 추가 조회 요청을 처리합니다(이후 API 다음 버전에서 위의 getInitialTweets() 메서드와 합치도록 리팩토링 예정).
     */
    @GetMapping("/timeline/next")
    public ResponseEntity<CollectionModel<TweetEntityModel>> getNextTweets(@RequestBody @Valid TweetLoadRequestDto tweetLoadRequestDto) {
        List<Tweet> nextTweets = tweetService.getNextTweets(tweetLoadRequestDto.getUserIdOfViewer(), tweetLoadRequestDto.getCreatedAtOfLastViewedTweet());

        List<TweetEntityModel> tweetModels = nextTweets.stream()
            .map(tweet -> {
                TweetEntityModel tweetEntityModel = new TweetEntityModel(tweet);

                tweetEntityModel.add(Link.of("/docs/index.html#get-tweet").withRel("profile"));

                return tweetEntityModel;
            })
            .collect(Collectors.toList());

        CollectionModel<TweetEntityModel> collectionModel = CollectionModel.of(tweetModels, linkTo(methodOn(TweetController.class).getNextTweets(tweetLoadRequestDto)).withSelfRel());

        collectionModel.add(Link.of("/docs/index.html#get-next-tweets").withRel("profile"));

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * 트윗의 상세내용 조회 요청을 처리합니다.
     */
    @GetMapping("/{tweetId}")
    public ResponseEntity<TweetEntityModel> getTweet(@PathVariable String tweetId) {
        Optional<Tweet> optionalTweet = tweetService.getTweet(tweetId);

        if (optionalTweet.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tweet tweet = optionalTweet.get();

        TweetEntityModel tweetEntityModel = new TweetEntityModel(tweet);

        tweetEntityModel.add(Link.of("/docs/index.html#get-tweet").withRel("profile"));

        return ResponseEntity.ok(tweetEntityModel);
    }

    /**
     * 트윗 포스팅 요청을 처리합니다.
     */
    @PostMapping
    public ResponseEntity<?> composeTweet(@RequestBody @Valid TweetComposeRequestDto tweetComposeDto, Errors errors) {
        if (errors.hasErrors()) {
            // return badRequest(errors); // 이후 적용 예정.
            return ResponseEntity.badRequest().build();
        }

        tweetValidator.validate(tweetComposeDto, errors);

        if (errors.hasErrors()) {
            // return badRequest(errors); // 이후 적용 예정.
            return ResponseEntity.badRequest().build();
        }

        Tweet tweet = modelMapper.map(tweetComposeDto, Tweet.class);

        Tweet newTweet = tweetService.composeTweet(tweet);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(TweetController.class).slash(newTweet.getId());

        URI createdUri = selfLinkBuilder.toUri();

        TweetEntityModel tweetEntityModel = new TweetEntityModel(newTweet);

        tweetEntityModel.add(linkTo(TweetController.class).slash("timeline").withRel("timeline-tweets"));

        tweetEntityModel.add(Link.of("/docs/index.html#resources-tweets-compose").withRel("profile"));

        return ResponseEntity.created(createdUri).body(tweetEntityModel);
    }

    /**
     * 트윗 삭제 요청을 처리합니다.
     */
    @DeleteMapping("/{tweetId}")
    public ResponseEntity<?> deleteTweet(@PathVariable String tweetId) {
        boolean deleted = tweetService.deleteTweet(tweetId);

        class EmptyEntityModel extends EntityModel<Void> {
            public EmptyEntityModel() {
                //add(linkTo(methodOn(TweetController.class).deleteTweet(tweetId)).withSelfRel());
                //
                add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

                add(Link.of("/docs/index.html#delete-tweet").withRel("profile"));
            }
        }

        if (deleted) {
            EmptyEntityModel responseEntityModel = new EmptyEntityModel();

            return ResponseEntity.ok().body(responseEntityModel);
        } else {
            HttpHeaders headers = new HttpHeaders();

            headers.setLocation(linkTo(TweetController.class).slash("timeline").toUri());

            return ResponseEntity.notFound().headers(headers).build();
        }
//
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setLocation(linkTo(TweetController.class).slash("timeline").toUri());
//
//        if (deleted) {
//            return ResponseEntity.noContent().headers(headers).build();
//        } else {
//            return ResponseEntity.notFound().headers(headers).build();
//        }
//
//        if (deleted) {
//            EntityModel<String> responseEntityModel = EntityModel.of("Tweet deleted");
//
//            responseEntityModel.add(linkTo(TweetController.class).slash(tweetId).withSelfRel());
//
//            responseEntityModel.add(linkTo(TweetController.class).slash("timeline").withRel("redirect-to-timeline-tweets"));
//
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseEntityModel);
//        } else {
//            EntityModel<String> responseEntityModel = EntityModel.of("Tweet not found");
//
//            responseEntityModel.add(linkTo(TweetController.class).slash(tweetId).withSelfRel());
//
//            responseEntityModel.add(linkTo(TweetController.class).slash("timeline").withRel("redirect-to-timeline-tweets"));
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseEntityModel);
//        }
    }
    //
    //// 이후 적용 예정.
    //private static ResponseEntity<ErrorEntityModel> badRequest(Errors errors) {
    //    return ResponseEntity.badRequest().body(new ErrorEntityModel(errors));
    //}
}
