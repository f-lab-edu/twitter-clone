package clone.twitter.controller;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_CONFLICT;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_CREATED;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_OK;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_UNAUTHORIZED;

import clone.twitter.annotation.AuthenticationCheck;
import clone.twitter.dto.request.UserDeleteRequestDto;
import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.SignInService;
import clone.twitter.service.UserService;
import clone.twitter.util.HttpResponseEntities;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    private final SignInService signInService;

    @PostMapping("/new")
    public ResponseEntity<Void> signUp(
        @RequestBody @Valid UserSignUpRequestDto userSignUpRequestDto) {

        userService.signUp(userSignUpRequestDto);

        return RESPONSE_CREATED;
    }

    @GetMapping("/username_exists")
    public ResponseEntity<Void> checkUniqueUsername(@PathVariable String username) {
        if (userService.isDuplicateUsername(username)) {
            return RESPONSE_CONFLICT;
        }

        return RESPONSE_OK;
    }

    @GetMapping("/email_exists")
    public ResponseEntity<Void> checkUniqueEmail(@PathVariable String email) {
        if (userService.isDuplicateEmail(email)) {
            return RESPONSE_CONFLICT;
        }

        return RESPONSE_OK;
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signInByUsername(
        @RequestBody @Valid UserSignInRequestDto userSigninRequestDto) {

        Optional<UserResponseDto> optionalUserResponseDto = userService.signIn(
            userSigninRequestDto);

        if (optionalUserResponseDto.isPresent()) {
            signInService.signInUser(optionalUserResponseDto.get().getUserId());

            return RESPONSE_OK;
        }

        return RESPONSE_UNAUTHORIZED;
    }

    @AuthenticationCheck
    @PostMapping("/signout")
    public ResponseEntity<Void> signOut() {
        signInService.signOutUser();

        return RESPONSE_OK;
    }

    @AuthenticationCheck
    @PostMapping("/{userId}/inactivate")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable String userId,
        @RequestBody UserDeleteRequestDto userDeleteRequestDto) {

        if (userService.deleteUserAccount(userId, userDeleteRequestDto.getPassword())) {
            signInService.signOutUser();

            return RESPONSE_OK;
        }

        return RESPONSE_UNAUTHORIZED;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable String userId) {
        Optional<UserResponseDto> optionalUserResponseDto = userService.getUserProfile(userId);

        if (optionalUserResponseDto.isEmpty()) {
            return HttpResponseEntities.notFound();
        }

        UserResponseDto userResponseDto = optionalUserResponseDto.get();

        return ResponseEntity.ok(userResponseDto);
    }
}

/*
 - 사용자 계정 삭제기능에 soft-delete를 적용하였습니다.
 - DB의 데이터를 hard-delete를 할 시:
   - 해당 엔티티와 참조 관계가 있던 다른 테이블의 엔티티에 영향을 끼쳐 데이터의 정합성에 문제가 생길 수도 있고,
   - 만약 사용자가 데이터를 삭제를 실수로 하였거나, 변심으로 복구를 원할 시 복원이 불가합니다.
   - 또한 비즈니스나 제품 기능 개선에 효용을 제공하는 정보들을 잃게 된다는 측면도 있습니다.
 - 반면 soft-delete로 구현 시:
   - 상기의 문제를 해결할 수 있지만,
   - 테이블에 삭제 상태를 표현하는 컬럼이 하나가 추가되어 데이터량이 증가하며(MySQL의 boolean/tinyint(1)은 1byte)
   - 향후 조회등 쿼리에 있어 모든 데이터에 대해 삭제 상태인지를 확인하는 쿼리가 추가되어 complexity가 증가합니다.
 - 두 방법 외 다른 선택지들에는 archiving, data anonymization, delayed hard-delete,
   log-based deletion 등이 있습니다.
 */
