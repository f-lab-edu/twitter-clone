package clone.twitter.controller;

import clone.twitter.dto.response.FollowResponseDto;
import clone.twitter.service.FollowService;
import lombok.RequiredArgsConstructor;
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
@RestController
@RequestMapping(value = "/users/{userId}/profile", produces = MediaTypes.HAL_JSON_VALUE)
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("follow/{followerId}")
    public ResponseEntity<FollowResponseModel> postFollow(@PathVariable String userId, @PathVariable String followerId) {
        FollowResponseDto followResponseDto = followService.follow(followerId, userId);

        FollowResponseModel followResponseModel = new FollowResponseModel(followResponseDto);

        followResponseModel.addProfileLink("user-follow");

        return ResponseEntity.ok(followResponseModel);
    }

    @DeleteMapping("follow/{followerId}")
    public ResponseEntity<FollowResponseModel> deleteFollow(@PathVariable String userId, @PathVariable String followerId) {
        FollowResponseDto followResponseDto = followService.unfollow(followerId, userId);

        FollowResponseModel followResponseModel = new FollowResponseModel(followResponseDto);

        followResponseModel.addProfileLink("user-unfollow");

        return ResponseEntity.ok(followResponseModel);
    }

    /**
     * 경우의 수는 3가지: 유저가 로그인이 되어있지 않은 경우(구현X), 로그인이 되어있고 조회하는 프로필의 유저가 자기자신인 경우, 로그인이 되어있고 조회하는 프로필의 유저가 타인인 경우. 첫 번째 경우에는 팔로우 여부가 표시되지 않음. 두 번째 경우 프로필 페이지 조회화면 rendering 시 본 메서드가 함께 호출되게 될 텐데, 이 때 followRequestDto의 followerId와 followeeId 값이 같게 되고, 이는 FE에서 처리하는 것으로 가정. 세 번째 경우 팔로우중 여부 표시.
     */
    @GetMapping("follow/{followerId}")
    public ResponseEntity<FollowResponseModel> getFollow(@PathVariable String userId, @PathVariable String followerId) {

        FollowResponseDto followResponseDto = followService.getFollow(followerId, userId);

        FollowResponseModel followResponseModel = new FollowResponseModel(followResponseDto);

        followResponseModel.addProfileLink("follow-info");

        return ResponseEntity.ok(followResponseModel);
    }
}
