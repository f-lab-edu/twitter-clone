package clone.twitter.controller;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_CREATED;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_NO_CONTENT;

import clone.twitter.annotation.AuthenticationCheck;
import clone.twitter.annotation.SignedInUserId;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.LikeTweetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets/{tweetId}/likes")
public class LikeTweetController {

    private final LikeTweetService likeTweetService;

    @AuthenticationCheck
    @PostMapping
    public ResponseEntity<Void> postLikeTweet(@PathVariable String tweetId, @SignedInUserId String userId) {
        likeTweetService.likeTweet(tweetId, userId);

        return RESPONSE_CREATED;
    }

    @AuthenticationCheck
    @DeleteMapping
    public ResponseEntity<Void> deleteLikeTweet(@PathVariable String tweetId, @SignedInUserId String userId) {
        likeTweetService.unlikeTweet(tweetId, userId);

        return RESPONSE_NO_CONTENT;
    }

    @AuthenticationCheck
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsersLikedTweet(@PathVariable String tweetId) {
        List<UserResponseDto> userResponseDtos = likeTweetService.getUsersLikedTweet(tweetId);

        return ResponseEntity.ok(userResponseDtos);
    }

    @AuthenticationCheck
    @GetMapping("/more")
    public ResponseEntity<?> getMoreUsersLikedTweet(@PathVariable String tweetId, @RequestParam String lastUserIdInCurrentBatch) {
        List<UserResponseDto> userResponseDtos = likeTweetService.getMoreUserLikedTweet(tweetId, lastUserIdInCurrentBatch);

        if (!userResponseDtos.isEmpty()) {
            return ResponseEntity.ok(userResponseDtos);
        }

        return RESPONSE_NO_CONTENT;
    }
}
