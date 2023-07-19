package clone.twitter.controller;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_BAD_REQUEST;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_NOT_FOUND;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_OK;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_UNAUTHORIZED;

import clone.twitter.domain.User;
import clone.twitter.dto.request.UserSigninRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.UserService;
import jakarta.validation.Valid;
import java.util.Optional;
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
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<Void> signUp(@RequestBody @Valid User user) {
        Optional<UserResponseDto> optionalUserResponseDto = userService.signUp(user);

        if (optionalUserResponseDto.isEmpty()) {
            // return badRequest(errors); // 이후 적용 예정.
            return RESPONSE_BAD_REQUEST;
        }

        return RESPONSE_OK;
    }

    /**
     * 사용자 프로필 정보 조회요청에 응답합니다. exception handling 이후 적용. exception handling needed for inserting
     * duplicate value to unique field for mybatis 'PersistenceException'. Or it can be handled by
     * trying to read specific value and let it return num of rows affected.
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<Void> getUserProfile(@PathVariable String userId) {
        Optional<UserResponseDto> optionalUserResponseDto = userService.getUserProfile(userId);

        if (optionalUserResponseDto.isEmpty()) {
            // return badRequest(errors); // 이후 적용 예정.
            return RESPONSE_NOT_FOUND;
        }

        return RESPONSE_OK;
    }

    /**
     * 사용자 로그인 요청에 응답합니다.
     */
    @PostMapping("/signin")
    public ResponseEntity<Void> signInByUsername(@RequestBody @Valid UserSigninRequestDto userSigninRequestDto, Errors errors) {
        Optional<UserResponseDto> optionalUserResponseDto = userService.signIn(userSigninRequestDto);

        if (optionalUserResponseDto.isEmpty()) {
            // return badRequest(errors); // 이후 적용 예정.
            return RESPONSE_UNAUTHORIZED;
        }

        return RESPONSE_OK;
    }

    /**
     * 사용자 계정 삭제 요청에 응답합니다. 이후 구현: 현재 회원가입이 안돼있을 시의 가장 최초 default index 페이지가 없는 상황(IndexModel의 index페이지도 로그인 된 유저의 timeline가 기준).
     */
    @DeleteMapping("/{userId}")
    public void deleteUserAccount(@PathVariable String userId) {
//        boolean deleted = userService.deleteUserAccount(userId);
//
//        if (deleted) {
//            // 현재 회원가입이 안돼있을 시의 가장 최초 default index 페이지가 없는 상황(IndexModel의 index페이지도 로그인 된 유저의 timeline가 기준). 이후 구현.
//            class EmptyEntityModel extends EntityModel<Void> {
//                public EmptyEntityModel() {
//                }
//            }
//
//            BaseModel baseModel = new BaseModel();
//
//            baseModel.add(Link.of("/docs/index.html#resources-users-delete-account").withRel("profile"));
//
//            return ResponseEntity.ok().body(baseModel);
//        } else {
//            // profile 페이지로 이동
//            HttpHeaders headers = new HttpHeaders();
//
//            headers.setLocation(linkTo(methodOn(UserController.class).getUserProfile(userId)).toUri());
//
//            return ResponseEntity.notFound().headers(headers).build();
//        }
    }
}

