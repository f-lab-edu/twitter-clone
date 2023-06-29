package clone.twitter.controller;

import clone.twitter.domain.LikeTweet;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.service.LikeTweetService;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/tweets/{tweetId}", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class LikeTweetController {
    @Autowired
    private final LikeTweetService likeTweetService;

    private static final ResponseEntity<Void> RESPONSE_CREATED = new ResponseEntity<>(HttpStatus.CREATED);

    /**
     * 개별 트윗에 대한 좋아요 포스팅 요청에 응답합니다. exception handling needed. self-descriptive message, HATEOAS 적용 유보.
     */
    @PostMapping("/like/users/{userId}")
    public ResponseEntity<Void> postLikeTweet(@PathVariable String tweetId, @PathVariable String userId) {
        likeTweetService.likeTweet(tweetId, userId);

        return RESPONSE_CREATED;
    }

    /**
     * 개별 트윗에 대한 좋아요 취소 요청에 응답합니다. exception handling needed.
     */
    @DeleteMapping("/like/users/{userId}")
    public ResponseEntity<LikeTweetResponseModel> deleteLikeTweet(@PathVariable String tweetId, @PathVariable String userId) {
        LikeTweetResponseDto likeTweetResponseDto = likeTweetService.unlikeTweet(tweetId, userId);

        LikeTweetResponseModel likeTweetResponseModel = new LikeTweetResponseModel(likeTweetResponseDto);

        likeTweetResponseModel.addProfileLink("unlike-tweet");

        return ResponseEntity.ok(likeTweetResponseModel);
    }
}
