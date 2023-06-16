package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.domain.LikeTweet;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.LikeTweetService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
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

    /**
     * (내부용 메서드)개별 '좋아요'에 대한 정보를 조회합니다.
     */
    public LikeTweet getLikeTweet(String tweetId, String userId) {
        Optional<LikeTweet> optionalLike = likeTweetService.getLikeTweet(tweetId, userId);

        return optionalLike.orElse(null);
    }

    /**
     * (controller 클래스 내부용 메서드, 향후 별도 클래스로 리팩토링 예정)UserResponseDto 리스트를 EntityModel 및 CollectionModel로 wrapping하고, 'fagmentIdentifier'를 docs의 url로 지정 및 추가하여 반환합니다. 향후 entity model에 관련한 기능들만 별도 branch로 만들어 본 메서드와 함께 refactoring 예정입니다(최상위 EntityModel class나 interface를 만들고 메서드로 포함 -> 하위 EntityModel class들이 상속하거나 구현(implement)하는 형태로 시도 예정)
     */
    private CollectionModel<UserResponseModel> convertToCollectionModel(
        List<UserResponseDto> userResponseDtos, String fragmentIdentifier) {
        List<UserResponseModel> userResponseModels = userResponseDtos.stream()
            .map(userResponseDto -> {
                UserResponseModel userResponseModel = new UserResponseModel(userResponseDto);

                // to be refactored to be method of UserResponseModel
                userResponseModel.add(Link.of("/docs/index.html#" + fragmentIdentifier).withRel("profile"));

                return userResponseModel;
            })
            .collect(Collectors.toList());

        CollectionModel<UserResponseModel> userResponseModelCollectionModel = CollectionModel.of(userResponseModels);

        userResponseModelCollectionModel.add(Link.of("/docs/index.html#" + fragmentIdentifier).withRel("profile"));

        return userResponseModelCollectionModel;
    }
}
