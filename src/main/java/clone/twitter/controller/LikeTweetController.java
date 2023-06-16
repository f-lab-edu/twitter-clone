package clone.twitter.controller;

import clone.twitter.domain.LikeTweet;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.service.LikeTweetService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
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

    /**
     * 개별 트윗에 대한 좋아요 포스팅 요청에 응답합니다. exception handling needed.
     */
    @PostMapping("/like/users/{userId}")
    public ResponseEntity<LikeTweetResponseModel> postLikeTweet(@PathVariable String tweetId, @PathVariable String userId) {
        LikeTweetResponseDto likeTweetResponseDto = likeTweetService.likeTweet(tweetId, userId);

        LikeTweetResponseModel likeTweetResponseModel = new LikeTweetResponseModel(likeTweetResponseDto);

        likeTweetResponseModel.addProfileLink("like-tweet");

        return ResponseEntity.ok(likeTweetResponseModel);
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

    /**
     * (내부용 메서드)개별 '좋아요'에 대한 정보를 조회합니다.
     */
    public LikeTweet getLikeTweet(String tweetId, String userId) {
        Optional<LikeTweet> optionalLike = likeTweetService.getLikeTweet(tweetId, userId);

        return optionalLike.orElse(null);
    }
}
