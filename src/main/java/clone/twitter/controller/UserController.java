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

    @GetMapping("/username/exists")
    public ResponseEntity<Void> checkDuplicateUsername(@RequestParam String username) {
        if (userService.isDuplicateUsername(username)) {
            return RESPONSE_CONFLICT;
        }

        return RESPONSE_OK;
    }

    @GetMapping("/email/exists")
    public ResponseEntity<Void> checkDuplicateEmail(@RequestParam String email) {
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
    @DeleteMapping("/{userId}")
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
