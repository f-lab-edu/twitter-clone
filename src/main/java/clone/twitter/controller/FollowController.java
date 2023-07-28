package clone.twitter.controller;

import clone.twitter.annotation.AuthenticationCheck;
import clone.twitter.annotation.SignedInUserId;
import clone.twitter.dto.request.UserFollowRequestDto;
import clone.twitter.dto.response.FollowResponseDto;
import clone.twitter.dto.response.UserFollowResponseDto;
import clone.twitter.service.FollowService;
import clone.twitter.util.HttpResponseEntities;
import clone.twitter.util.UserFollowRequestDtoValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    private final UserFollowRequestDtoValidator followUsersRequestDtoValidator;

    @AuthenticationCheck
    @PostMapping("/{followeeId}")
    public ResponseEntity<FollowResponseDto> postFollow(@SignedInUserId String userId, @PathVariable String followeeId) {
        FollowResponseDto followResponseDto = followService.follow(userId, followeeId);

        return ResponseEntity.ok(followResponseDto);
    }

    @AuthenticationCheck
    @DeleteMapping("/{followeeId}")
    public ResponseEntity<FollowResponseDto> deleteFollow(@SignedInUserId String userId, @PathVariable String followeeId) {
        FollowResponseDto followResponseDto = followService.unfollow(userId, followeeId);

        return ResponseEntity.ok(followResponseDto);
    }

    @AuthenticationCheck
    @PostMapping
    public ResponseEntity<?> getUserFollowList(@RequestBody @Valid UserFollowRequestDto userFollowRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return HttpResponseEntities.RESPONSE_BAD_REQUEST;
        }

        followUsersRequestDtoValidator.validate(userFollowRequestDto, errors);

        if (errors.hasErrors()) {
            return HttpResponseEntities.RESPONSE_BAD_REQUEST;
        }

        List<UserFollowResponseDto> usersFollowResponseDtos = followService.getUserFollowList(userFollowRequestDto);

        if (!usersFollowResponseDtos.isEmpty()) {
            return ResponseEntity.ok(usersFollowResponseDtos);
        }

        return HttpResponseEntities.RESPONSE_NO_CONTENT;
    }

    @AuthenticationCheck
    @GetMapping("/{followerId}/{followeeId}")
    public ResponseEntity<FollowResponseDto> getFollow(@PathVariable String followerId, @PathVariable String followeeId) {
        FollowResponseDto followResponseDto = followService.getFollow(followerId, followeeId);

        return ResponseEntity.ok(followResponseDto);
    }
}
