package clone.twitter.controller;

import clone.twitter.annotation.AuthenticationCheck;
import clone.twitter.annotation.SignedInUserId;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.LikeTweetService;
import clone.twitter.util.HttpResponseEntities;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<LikeTweetResponseDto> postLikeTweet(@PathVariable String tweetId, @SignedInUserId String userId) {
        LikeTweetResponseDto likeTweetResponseDto = likeTweetService.likeTweet(tweetId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeTweetResponseDto);
    }

    @AuthenticationCheck
    @DeleteMapping
    public ResponseEntity<LikeTweetResponseDto> deleteLikeTweet(@PathVariable String tweetId, @SignedInUserId String userId) {
        LikeTweetResponseDto likeTweetResponseDto = likeTweetService.unlikeTweet(tweetId, userId);

        return ResponseEntity.ok(likeTweetResponseDto);
    }

    @AuthenticationCheck
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsersLikedTweet(@PathVariable String tweetId) {
        List<UserResponseDto> userResponseDtos = likeTweetService.getUsersLikedTweet(tweetId);

        return ResponseEntity.ok(userResponseDtos);
    }

    @AuthenticationCheck
    @PostMapping("/more")
    public ResponseEntity<List<UserResponseDto>> getMoreUsersLikedTweet(@PathVariable String tweetId, @RequestParam String userIdOfUserLastOnList) {
        List<UserResponseDto> userResponseDtos = likeTweetService.getMoreUserLikedTweet(tweetId, userIdOfUserLastOnList);

        if (!userResponseDtos.isEmpty()) {
            return ResponseEntity.ok(userResponseDtos);
        }

        return HttpResponseEntities.noContent();
    }
}
