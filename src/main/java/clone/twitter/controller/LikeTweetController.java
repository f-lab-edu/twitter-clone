package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.LikeTweetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final ResponseEntity<Void> RESPONSE_NO_CONTENT = new ResponseEntity<>(HttpStatus.NO_CONTENT);

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
    public ResponseEntity<Void> deleteLikeTweet(@PathVariable String tweetId, @PathVariable String userId) {
        likeTweetService.unlikeTweet(tweetId, userId);

        return RESPONSE_NO_CONTENT;
    }

    /**
     * 개별 트윗에 좋아요를 표시한 유저목록의 최초 조회 요청에 응답합니다.
     */
    @GetMapping("/like/users")
    public ResponseEntity<CollectionModel<UserResponseModel>> getUsersLikedTweet(@PathVariable String tweetId) {
        List<UserResponseDto> userResponseDtos = likeTweetService.getUsersLikedTweet(tweetId);

        String docsFragmentIdentifier = "users-liked-tweet";

        CollectionModel<UserResponseModel> userResponseCollectionModel = convertToCollectionModel(userResponseDtos, docsFragmentIdentifier);

        userResponseCollectionModel.add(linkTo(methodOn(LikeTweetController.class).getUsersLikedTweet(tweetId)).withSelfRel());

        userResponseCollectionModel.add(linkTo(methodOn(LikeTweetController.class).getMoreUsersLikedTweet(tweetId, userResponseDtos.get(userResponseDtos.size() - 1).getUserId())).withRel("more-users-liked-tweet"));

        userResponseCollectionModel.add(linkTo(methodOn(TweetController.class).getTweet(tweetId)).withRel("tweet"));

        return ResponseEntity.ok(userResponseCollectionModel);
    }

    /**
     * 개별 트윗에 좋아요를 표시한 유저목록의 추가 조회 요청에 응답합니다. (self의 경우 어떤 링크를 줘야하는지?그냥 본 메서드의 링크를 주면 현재 단계에서 더 로드를 하는 게 아닌지...)
     */
    @GetMapping("/like/users/{userIdLastOnList}")
    public ResponseEntity<CollectionModel<UserResponseModel>> getMoreUsersLikedTweet(@PathVariable String tweetId, @PathVariable String userIdLastOnList) {
        List<UserResponseDto> userResponseDtos = likeTweetService.getMoreUserLikedTweet(tweetId, userIdLastOnList);

        if (!userResponseDtos.isEmpty()) {
            String docsFragmentIdentifier = "more-users-liked-tweet";

            CollectionModel<UserResponseModel> userResponseCollectionModel = convertToCollectionModel(userResponseDtos, docsFragmentIdentifier);

            userResponseCollectionModel.add(linkTo(methodOn(TweetController.class).getTweet(tweetId)).withRel("tweet"));

            return ResponseEntity.ok(userResponseCollectionModel);
        }

        return ResponseEntity.noContent().build();
    }
}
