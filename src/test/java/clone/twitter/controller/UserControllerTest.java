package clone.twitter.controller;

import static clone.twitter.common.DataGenerationHelper.CREATED_AT_OFFSET;
import static clone.twitter.common.DataGenerationHelper.INDEX_OFFSET;
import static clone.twitter.common.DataGenerationHelper.generateUserSignUpRequestDto;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.BaseControllerTest;
import clone.twitter.dto.request.UserDeleteRequestDto;
import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.UserServiceWithSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class UserControllerTest extends BaseControllerTest {

    @Autowired
    UserServiceWithSession userService;

    UserSignUpRequestDto userSignUpRequestDto;

    @BeforeEach
    protected void init() {

        // user 회원가입 기본 데이터 생성
        userSignUpRequestDto = generateUserSignUpRequestDto(
            INDEX_OFFSET, CREATED_AT_OFFSET);
    }

    @Test
    @DisplayName("회원가입 정상 완료")
    void signUp200() throws Exception {

        // email 중복여부 조회
        this.mockMvc.perform(get("/users/email/exists")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("email", userSignUpRequestDto.getEmail()));

        // 회원가입 정상 완료
        this.mockMvc.perform(post("/users/new")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userSignUpRequestDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("sign-up",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                requestFields(
                    fieldWithPath("username").description("사용자의 username 필드값"),
                    fieldWithPath("email").description("사용자의 email 필드값"),
                    fieldWithPath("password").description("사용자의 입력 password 필드값"),
                    fieldWithPath("profileName").description("사용자의 profile name 필드값"),
                    fieldWithPath("birthdate").description("사용자의 생년월일 필드값")
                )
            ));
    }

    @Test
    @DisplayName("username 중복 아닐시 OK 반환")
    void checkDuplicateUsername200() throws Exception {

        // username 중복여부 조회
        this.mockMvc.perform(get("/users/username/exists")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("username", userSignUpRequestDto.getUsername()))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("check-duplicate-username-ok",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                queryParameters(
                    parameterWithName("username").description("사용자의 username 필드값")
                )
            ));
    }

    @Test
    @DisplayName("username 중복시 CONFLICT 반환")
    void checkDuplicateUsername409() throws Exception {

        // user 회원가입, DB 저장
        userService.signUp(userSignUpRequestDto);

        // DB에 해당 username 이미 있는지 조회
        this.mockMvc.perform(get("/users/username/exists")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("username", userSignUpRequestDto.getUsername()))
            .andDo(print())
            .andExpect(status().isConflict())
            .andDo(document("check-duplicate-username-conflict",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                queryParameters(
                    parameterWithName("username").description("사용자의 username 필드값")
                )
            ));
    }

    @Test
    @DisplayName("email 중복이 아닐시 OK 반환")
    void checkDuplicateEmail200() throws Exception {

        // email 중복여부 조회
        this.mockMvc.perform(get("/users/email/exists")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("email", userSignUpRequestDto.getEmail()))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("check-duplicate-email-ok",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                queryParameters(
                    parameterWithName("email").description("사용자의 email 필드값")
                )
            ));
    }

    @Test
    @DisplayName("email 중복시 CONFLICT 반환")
    void checkDuplicateEmail409() throws Exception {

        // user 회원가입, DB 저장
        userService.signUp(userSignUpRequestDto);

        // DB에 해당 email 이미 있는지 조회
        this.mockMvc.perform(get("/users/email/exists")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("email", userSignUpRequestDto.getEmail()))
            .andDo(print())
            .andExpect(status().isConflict())
            .andDo(document("check-duplicate-email-conflict",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                queryParameters(
                    parameterWithName("email").description("사용자의 email 필드값")
                )
            ));
    }

    @Test
    @DisplayName("로그인시 email 및 password 일치시 상태코드 OK 반환")
    void signIn200() throws Exception {

        // user 회원가입, DB 저장
        userService.signUp(userSignUpRequestDto);

        // email + password로 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password(userSignUpRequestDto.getPassword())
            .build();

        // email + password로 로그인
        this.mockMvc.perform(post("/users/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userSignInRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("sign-in-ok",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                requestFields(
                    fieldWithPath("email").description("사용자의 email 필드값"),
                    fieldWithPath("password").description("사용자의 입력 password 필드값")
                )
            ));
    }

    @Test
    @DisplayName("로그인시 email이 존재하지 않거나, email 또는 password 불일치시 상태코드 UNAUTHORIZED 반환")
    void signIn401() throws Exception {

        // user 회원가입, DB 저장
        userService.signUp(userSignUpRequestDto);

        // 1. 유효하지 않은 email 로그인 시도
        // email + password로 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDtoWithInvalidEmail = UserSignInRequestDto.builder()
            .email("INVALID_EMAIL@twitter.com")
            .password(userSignUpRequestDto.getPassword())
            .build();

        this.mockMvc.perform(post("/users/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userSignInRequestDtoWithInvalidEmail)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andDo(document("sign-in-with-invalid-email-unauthorized",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                requestFields(
                    fieldWithPath("email").description("사용자의 email 필드값"),
                    fieldWithPath("password").description("사용자의 입력 password 필드값")
                )
            ));

        // 2. 유효하지 않은 password 로그인 시도
        // email + 불일치 password 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDtoWithInvalidPassword = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password("WRONG_PASSWORD")
            .build();

        this.mockMvc.perform(post("/users/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userSignInRequestDtoWithInvalidPassword)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andDo(document("sign-in-with-invalid-password-unauthorized",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                requestFields(
                    fieldWithPath("email").description("사용자의 email 필드값"),
                    fieldWithPath("password").description("사용자의 입력 password 필드값")
                )
            ));
    }

    @Test
    @DisplayName("user 계정 삭제 정상입력시 OK 반환")
    void deleteUserAccount200() throws Exception {

        // user 회원가입, DB 저장
        userService.signUp(userSignUpRequestDto);

        // 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password(userSignUpRequestDto.getPassword())
            .build();

        // 로그인 사용자 정보 획득
        UserResponseDto userResponseDto = userService.signIn(userSignInRequestDto);

        // user 계정 존재여부 검증
        Assertions.assertThat(userResponseDto).isNotNull();

        // 계정삭제 폼 입력
        UserDeleteRequestDto userDeleteRequestDto = UserDeleteRequestDto.builder()
            .password(userSignInRequestDto.getPassword())
            .build();

        this.mockMvc.perform(delete("/users/{userId}", userResponseDto.getUserId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userDeleteRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("delete-user-ok",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                pathParameters(
                    parameterWithName("userId").description("사용자의 userId 필드값")
                ),
                requestFields(
                    fieldWithPath("password").description("사용자의 입력 password 필드값")
                )
            ));
    }

    @Test
    @DisplayName("user 계정 삭제 비밀번호 불일치시 UNAUTHORIZED 반환")
    void deleteUserAccount401() throws Exception {

        // user 회원가입 및 DB 저장
        userService.signUp(userSignUpRequestDto);

        // 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password(userSignUpRequestDto.getPassword())
            .build();

        // 로그인 사용자 정보 획득
        UserResponseDto userResponseDto = userService.signIn(userSignInRequestDto);

        // user 계정 존재여부 검증
        Assertions.assertThat(userResponseDto).isNotNull();

        // 계정삭제 폼 입력
        UserDeleteRequestDto userDeleteRequestDto = UserDeleteRequestDto.builder()
            .password("WRONG_PASSWORD")
            .build();

        this.mockMvc.perform(delete("/users/{userId}", userResponseDto.getUserId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userDeleteRequestDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andDo(document("delete-user-unauthorized",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                pathParameters(
                    parameterWithName("userId").description("사용자의 userId 필드값")
                ),
                requestFields(
                    fieldWithPath("password").description("사용자의 입력 password 필드값")
                )
            ));
    }

    @Test
    @DisplayName("존재하지 않는 계정 삭제 등 비정상 요청시 BAD_REQUEST 반환")
    void deleteUserAccount400() throws Exception {

        // user 회원가입 및 DB 저장
        userService.signUp(userSignUpRequestDto);

        // 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password(userSignUpRequestDto.getPassword())
            .build();

        // 로그인 사용자 정보 획득
        UserResponseDto userResponseDto = userService.signIn(userSignInRequestDto);

        // user 계정 존재 검증
        Assertions.assertThat(userResponseDto).isNotNull();

        // 계정삭제 폼 입력
        UserDeleteRequestDto userDeleteRequestDto = UserDeleteRequestDto.builder()
            .password(userSignInRequestDto.getPassword())
            .build();

        // 계정 삭제
        userService.deleteUserAccount(userResponseDto.getUserId(),
            userSignInRequestDto.getPassword());

        // 이미 삭제된 존재하지 않은 계정 삭제 시도
        this.mockMvc.perform(delete("/users/{userId}", userResponseDto.getUserId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(userDeleteRequestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andDo(document("delete-user-bad-request",
                requestHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("요청의 content type")
                ),
                pathParameters(
                    parameterWithName("userId").description("사용자의 userId 필드값")
                ),
                requestFields(
                    fieldWithPath("password").description("사용자의 입력 password 필드값")
                )
            ));
    }

    @Test
    @DisplayName("user 프로필 조회 성공 OK 반환")
    void getUserProfile200() throws Exception {

        // user 회원가입 및 DB 저장
        userService.signUp(userSignUpRequestDto);

        // 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password(userSignUpRequestDto.getPassword())
            .build();

        // 로그인 사용자 정보 획득
        UserResponseDto userResponseDto = userService.signIn(userSignInRequestDto);

        // DB에 해당 email 이미 있는지 조회
        this.mockMvc.perform(get("/users/{userId}", userResponseDto.getUserId())
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-user-profile-ok",
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("응답에서 받을 수 있는 content type")
                ),
                pathParameters(
                    parameterWithName("userId").description("사용자의 userId 필드값")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("응답의 content type")
                ),
                responseFields(
                    fieldWithPath("userId").description("사용자의 userId 필드값"),
                    fieldWithPath("username").description("사용자의 username 필드값"),
                    fieldWithPath("profileName").description("사용자의 profileName 필드값"),
                    fieldWithPath("createdDate").description("사용자의 계정 생성일 필드값")
                )
            ));
    }

    @Test
    @DisplayName("user 프로필 조회 실패 NOT FOUND 반환")
    void getUserProfile404() throws Exception {

        // user 회원가입 및 DB 저장
        userService.signUp(userSignUpRequestDto);

        // 로그인 정보 생성
        UserSignInRequestDto userSignInRequestDto = UserSignInRequestDto.builder()
            .email(userSignUpRequestDto.getEmail())
            .password(userSignUpRequestDto.getPassword())
            .build();

        // 로그인 사용자 정보 획득
        UserResponseDto userResponseDto = userService.signIn(userSignInRequestDto);

        // 유효하지 않은 userId 검증
        String invalidUserId = "INVALID_USER_ID";
        Assertions.assertThat(userResponseDto.getUserId()).isNotEqualTo(invalidUserId);

        // 유효하지 않은 userId로 user 프로필 조회
        this.mockMvc.perform(get("/users/{userId}", invalidUserId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("get-user-profile-not-found",
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("응답에서 받을 수 있는 content type")
                ),
                pathParameters(
                    parameterWithName("userId").description("사용자의 userId 필드값")
                )
            ));
    }
}
