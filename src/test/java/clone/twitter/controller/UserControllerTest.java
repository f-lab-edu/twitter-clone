package clone.twitter.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.BaseControllerTest;
import clone.twitter.domain.User;
import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class UserControllerTest extends BaseControllerTest {
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("POST /users/new - 회원가입: 정상 응답")
    void signUp() throws Exception {
        // given
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .username("username1")
            .email("user1@twitter.com")
            .passwordHash("PA$$W0RDHA$H3D")
            .profileName("user1-profile-name")
            .birthdate(LocalDate.of(1990, 1, 1))
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .updatedAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        // when & then
        this.mockMvc.perform(post("/users/new")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("profileName").exists())
            .andExpect(jsonPath("createdDate").exists())
            .andExpect(jsonPath("userId").isNotEmpty())
            .andExpect(jsonPath("username").isNotEmpty())
            .andExpect(jsonPath("profileName").isNotEmpty())
            .andExpect(jsonPath("createdDate").isNotEmpty())
            .andExpect(jsonPath("$._links.user-profile.href").exists())
            .andExpect(jsonPath("$._links.timeline.href").exists())
            .andExpect(jsonPath("$._links.profile.href").exists())
            .andDo(document("user-sign-up",
                links(
                    linkWithRel("user-profile").description("link to user profile"),
                    linkWithRel("timeline").description("link to tweet timeline"),
                    linkWithRel("profile").description("link to docs profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("id").description("identifier of user account"),
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("email").description("email of user account"),
                    fieldWithPath("passwordHash").description("encoded password of user account"),
                    fieldWithPath("profileName").description("profile name of user account"),
                    fieldWithPath("birthdate").description("birthdate of user account"),
                    fieldWithPath("createdAt").description("created date and time of user account"),
                    fieldWithPath("updatedAt").description("updated date and time of user account")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("userId").description("identifier of user account"),
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("profileName").description("profile name of user account"),
                    fieldWithPath("createdDate").description("created date of user account"),
                    fieldWithPath("_links.user-profile.href").description("link to user profile"),
                    fieldWithPath("_links.timeline.href").description("link to timeline"),
                    fieldWithPath("_links.profile.href").description("link to docs profile")
                )
            ));
    }

    @Test
    @DisplayName("GET /users/{userId}/profile - user id로 회원 조회: 정상 응답")
    void getUserProfile() throws Exception {
        // given
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .username("username1")
            .email("user1@twitter.com")
            .passwordHash("PA$$W0RDHA$H3D")
            .profileName("user1-profile-name")
            .birthdate(LocalDate.of(1990, 1, 1))
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .updatedAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        userRepository.save(user);

        // when & then
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{userId}/profile", user.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("profileName").exists())
            .andExpect(jsonPath("createdDate").exists())
            .andExpect(jsonPath("userId").isNotEmpty())
            .andExpect(jsonPath("username").isNotEmpty())
            .andExpect(jsonPath("profileName").isNotEmpty())
            .andExpect(jsonPath("createdDate").isNotEmpty())
            .andExpect(jsonPath("$._links.user-profile.href").exists())
            .andExpect(jsonPath("$._links.timeline.href").exists())
            .andExpect(jsonPath("$._links.profile.href").exists())
            .andDo(document("get-user-profile",
                links(
                    linkWithRel("user-profile").description("link to user profile"),
                    linkWithRel("timeline").description("link to tweet timeline"),
                    linkWithRel("profile").description("link to docs profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("userId").description("identifier of user account")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("userId").description("identifier of user account"),
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("profileName").description("profile name of user account"),
                    fieldWithPath("createdDate").description("created date of user account"),
                    fieldWithPath("_links.user-profile.href").description("link to user profile"),
                    fieldWithPath("_links.timeline.href").description("link to timeline"),
                    fieldWithPath("_links.profile.href").description("link to docs profile")
                )
            ));
    }

    @Test
    @DisplayName("POST /users/signin - username으로 로그인: 정상 응답")
    void signInWithUsername() throws Exception {
        // given
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .username("username1")
            .email("user1@twitter.com")
            .passwordHash("PA$$W0RDHA$H3D")
            .profileName("user1-profile-name")
            .birthdate(LocalDate.of(1990, 1, 1))
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .updatedAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        userRepository.save(user);

        UserSignInRequestDto userSigninRequestDto = UserSignInRequestDto.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .build();

        // when & then
        this.mockMvc.perform(post("/users/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(userSigninRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("profileName").exists())
            .andExpect(jsonPath("createdDate").exists())
            .andExpect(jsonPath("userId").isNotEmpty())
            .andExpect(jsonPath("username").isNotEmpty())
            .andExpect(jsonPath("profileName").isNotEmpty())
            .andExpect(jsonPath("createdDate").isNotEmpty())
            .andExpect(jsonPath("$._links.user-profile.href").exists())
            .andExpect(jsonPath("$._links.timeline.href").exists())
            .andExpect(jsonPath("$._links.profile.href").exists())
            .andDo(document("user-sign-in-with-username",
                links(
                    linkWithRel("user-profile").description("link to user profile"),
                    linkWithRel("timeline").description("link to tweet timeline"),
                    linkWithRel("profile").description("link to docs profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("email").description("email of user account"),
                    fieldWithPath("password").description("encoded password of user account")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("userId").description("identifier of user account"),
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("profileName").description("profile name of user account"),
                    fieldWithPath("createdDate").description("created date of user account"),
                    fieldWithPath("_links.user-profile.href").description("link to user profile"),
                    fieldWithPath("_links.timeline.href").description("link to timeline"),
                    fieldWithPath("_links.profile.href").description("link to docs profile")
                )
            ));
    }

    @Test
    @DisplayName("POST /users/signin - email로 로그인: 정상 응답")
    void signInWithEmail() throws Exception {
        // given
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .username("username1")
            .email("user1@twitter.com")
            .passwordHash("PA$$W0RDHA$H3D")
            .profileName("user1-profile-name")
            .birthdate(LocalDate.of(1990, 1, 1))
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .updatedAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        userRepository.save(user);

        UserSignInRequestDto userSigninRequestDto = UserSignInRequestDto.builder()
            .email(user.getEmail())
            .password(user.getPasswordHash())
            .build();

        // when & then
        this.mockMvc.perform(post("/users/signin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(userSigninRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("username").exists())
            .andExpect(jsonPath("profileName").exists())
            .andExpect(jsonPath("createdDate").exists())
            .andExpect(jsonPath("userId").isNotEmpty())
            .andExpect(jsonPath("username").isNotEmpty())
            .andExpect(jsonPath("profileName").isNotEmpty())
            .andExpect(jsonPath("createdDate").isNotEmpty())
            .andExpect(jsonPath("$._links.user-profile.href").exists())
            .andExpect(jsonPath("$._links.timeline.href").exists())
            .andExpect(jsonPath("$._links.profile.href").exists())
            .andDo(document("user-sign-in-with-email",
                links(
                    linkWithRel("user-profile").description("link to user profile"),
                    linkWithRel("timeline").description("link to tweet timeline"),
                    linkWithRel("profile").description("link to docs profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("email").description("email of user account"),
                    fieldWithPath("password").description("encoded password of user account")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("userId").description("identifier of user account"),
                    fieldWithPath("username").description("username of user account"),
                    fieldWithPath("profileName").description("profile name of user account"),
                    fieldWithPath("createdDate").description("created date of user account"),
                    fieldWithPath("_links.user-profile.href").description("link to user profile"),
                    fieldWithPath("_links.timeline.href").description("link to timeline"),
                    fieldWithPath("_links.profile.href").description("link to docs profile")
                )
            ));
    }

    /**
     * 이후 구현: 현재 회원가입이 안돼있을 시의 가장 최초 default index 페이지가 없는 상황(IndexModel의 index페이지도 로그인 된 유저의 timeline가 기준).
     */
    @Test
    @DisplayName("DELETE /users/{id} - user id로 계정 삭제: 정상 응답")
    void deleteAccount() throws Exception {
//        // given
//        User user = User.builder()
//            .id(UUID.randomUUID().toString())
//            .username("username1")
//            .email("user1@twitter.com")
//            .passwordHash("PA$$W0RDHA$H3D")
//            .profileName("user1-profile-name")
//            .birthdate(LocalDate.of(1990, 1, 1))
//            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
//            .updatedAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
//            .build();
//
//        userRepository.save(user);
//
//        // when & then
//        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/{userId}", user.getId())
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaTypes.HAL_JSON))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$._links.profile.href").exists())
//            .andDo(document("delete-user-account",
//                links(
//                    linkWithRel("profile").description("link to docs profile")
//                ),
//                requestHeaders(
//                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
//                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
//                ),
//                pathParameters(
//                    parameterWithName("userId").description("identifier of user account")
//                ),
//                responseHeaders(
//                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
//                ),
//                responseFields(
//                    fieldWithPath("_links.profile.href").description("link to docs profile")
//                )
//            ));
    }
}
