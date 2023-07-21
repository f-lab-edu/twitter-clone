package clone.twitter.controller;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_CONFLICT;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_CREATED;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_OK;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_UNAUTHORIZED;

import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.UserService;
import clone.twitter.util.HttpResponseEntities;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<Void> signUp(@RequestBody @Valid UserSignUpRequestDto userSignUpRequestDto) {
        userService.signUp(userSignUpRequestDto);

        return RESPONSE_CREATED;
    }

    @GetMapping("/{username}/exists")
    public ResponseEntity<Void> checkUniqueUsername(@PathVariable String username) {
        if (userService.isUniqueUsername(username)) {
            return RESPONSE_CONFLICT;
        }

        return RESPONSE_OK;
    }

    @GetMapping("/{email}/exists")
    public ResponseEntity<Void> checkUniqueEmail(@PathVariable String email) {
        if (userService.isUniqueEmail(email)) {
            return RESPONSE_CONFLICT;
        }

        return RESPONSE_OK;
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signInByUsername(@RequestBody @Valid UserSignInRequestDto userSigninRequestDto) {
        UserResponseDto optionalUserResponseDto = userService.signIn(userSigninRequestDto);

        if (optionalUserResponseDto != null) {
            // 별도 SignInService 구현 및 추가 로그인 처리

            return RESPONSE_OK;
        }

        return RESPONSE_UNAUTHORIZED;
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut() {
        // 별도 SignInService 구현 및 추가 로그인 처리

        return RESPONSE_OK;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable String userId, @RequestParam String inputPassword) {
        if (userService.deleteUserAccount(userId, inputPassword)) {
            return RESPONSE_OK;
        }

        return RESPONSE_UNAUTHORIZED;
    }

    // 로그인 불필요
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable String userId) {
        Optional<UserResponseDto> optionalUserResponseDto = userService.getUserProfile(userId);

        if (optionalUserResponseDto.isEmpty()) {
            return HttpResponseEntities.notFound();
        }

        UserResponseDto userResponseDto = optionalUserResponseDto.get();

        return ResponseEntity.ok(userResponseDto);
    }
}

