package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetLoadRequestDto;
import clone.twitter.dto.request.TweetPostRequestDto;
import clone.twitter.service.TweetService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/tweets", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class TweetController {
    @Autowired
    private final TweetService tweetService;

    @Autowired
    private final ModelMapper modelMapper;

    // 타임라인의 최초 트윗 목록 조회 요청을 처리합니다. 이후 API 다음 버전에서 아래의 getNextTweets() 메서드와 합치도록 리팩토링 예정입니다.
    @GetMapping("/timeline")
    public List<Tweet> getInitialTweets(@RequestBody String userId) {
        return tweetService.getInitialTweets(userId);
    }

    // 이전 트윗목록의 끝까지 모두 조회했을 시 트윗목록을 추가 조회 요청을 처리합니다. 이후 API 다음 버전에서 위의 getInitialTweets() 메서드와 합치도록 리팩토링 예정입니다.
    @GetMapping("/timeline/next")
    public List<Tweet> getNextTweets(@RequestBody @Valid TweetLoadRequestDto tweetLoadRequestDto) {
        return tweetService.getNextTweets(tweetLoadRequestDto.getUserId(), tweetLoadRequestDto.getCreatedAtOfLastTweet());
    }

    // 트윗의 상세내용 요청을 처리합니다.
    @GetMapping("/{tweetId}")
    public Optional<Tweet> getTweet(@PathVariable String tweetId) {
        return tweetService.getTweet(tweetId);
    }

    // 트윗 포스팅 요청을 처리합니다.
    @PostMapping
    public ResponseEntity postTweet(@RequestBody @Valid TweetPostRequestDto tweetPostRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Tweet tweet = modelMapper.map(tweetPostRequestDto, Tweet.class);
        ////ModelMapper를 사용하지 않을 시 아래와 같이 구현 가능하며, 해당 경우 성능상 이점 외에 tweetRequestDto의 필드 설정을 통해 원하지 않는 릴드와 값이 추가로 들어오는 걸 원천적으로 막을 수 있는 이점이 있다.
        //Tweet tweet = Tweet.builder()
        //    .text(tweetRequestDto.getText())
        //    .userId(tweetRequestDto.getUserId())
        //    .build();

        Tweet newTweet = tweetService.postTweet(tweet);

        URI createdUri = linkTo(TweetController.class).slash(newTweet.getId()).toUri();

        return ResponseEntity.created(createdUri).body(newTweet);
    }

    // 트윗 삭제 요청을 처리합니다.
    @DeleteMapping("/{tweetId}")
    public void deleteTweet(@PathVariable String tweetId) {
        tweetService.deleteTweet(tweetId);
    }
}
