package clone.twitter.controller;

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
@RequestMapping("/users/{userId}")
public class FollowController {

    private FollowService followService;

    private UserFollowRequestDtoValidator followUsersRequestDtoValidator;

    @PostMapping("follow/{followerId}")
    public ResponseEntity<FollowResponseDto> postFollow(@PathVariable String userId, @PathVariable String followerId) {
        FollowResponseDto followResponseDto = followService.follow(followerId, userId);

        return ResponseEntity.ok(followResponseDto);
    }

    @DeleteMapping("follow/{followerId}")
    public ResponseEntity<FollowResponseDto> deleteFollow(@PathVariable String userId, @PathVariable String followerId) {
        FollowResponseDto followResponseDto = followService.unfollow(followerId, userId);

        return ResponseEntity.ok(followResponseDto);
    }

    @PostMapping("/follows")
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

    @GetMapping("follow/{followerId}")
    public ResponseEntity<FollowResponseDto> getFollow(@PathVariable String userId, @PathVariable String followerId) {
        FollowResponseDto followResponseDto = followService.getFollow(followerId, userId);

        return ResponseEntity.ok(followResponseDto);
    }
}
