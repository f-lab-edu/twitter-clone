package clone.twitter.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.BaseControllerTest;
import clone.twitter.domain.Follow;
import clone.twitter.domain.User;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class FollowControllerTest extends BaseControllerTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    static final int BEGINNING_INDEX_OF_STREAM_RANGE = 0;

    static final LocalDateTime BASE_CREATED_AT = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(
        ChronoUnit.SECONDS);

    @Test
    @DisplayName("GET /users/{userId}/profile/follow/{followerId} - 본인과 특정 유저와의 팔로우관계 정보 조회")
    void getFollow() throws Exception {
        // given
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, 2)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        Follow follow = this.generateFollow(users.get(0), users.get(1));

        // when & then
        this.mockMvc.perform(get("/users/{userId}/profile/follow/{followerId}", users.get(1).getId(), users.get(0))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("followerId").isNotEmpty())
            .andExpect(jsonPath("followeeId").isNotEmpty())
            .andExpect(jsonPath("isFollowing").isNotEmpty())
            .andExpect(jsonPath("$._links.user-profile-page.href").isNotEmpty())
            .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
            .andDo(document("get-follow-info",
                links(
                    linkWithRel("user-profile-page").description("link to user(follower) profile page"),
                    linkWithRel("profile").description("link to docs profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("followerId").description("identifier of user account following a target user account"),
                    parameterWithName("userId").description("identifier of the followee, or target user account being followed by browsing user account")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("followerId").description("identifier of user account following a target user account"),
                    fieldWithPath("followeeId").description("identifier of target user account being followed by browsing user account"),
                    fieldWithPath("isFollowing").description("information whether the browsing user is following the viewed target user or not"),
                    fieldWithPath("_links.user-profile-page.href").description("link to user(follower) profile page"),
                    fieldWithPath("_links.profile.href").description("ink to docs profile")
                )
            ));
    }

    private User generateUser(int index, LocalDateTime baseCreatedAt) {
        User user = User.builder()
            .id("user" + (index + 1))
            .username("username" + (index + 1))
            .email("user" + (index + 1) + "@twitter.com")
            .passwordHash("password" + (index + 1))
            .profileName("userProfileName" + (index + 1))
            .birthdate(baseCreatedAt.minusYears(20 + index).toLocalDate())
            .createdAt(baseCreatedAt.plusSeconds(index))
            .updatedAt(baseCreatedAt.plusSeconds(index))
            .build();

        userRepository.save(user);

        return user;
    }

    private Follow generateFollow(User follower, User followee) {
        LocalDateTime followCreatedAt = follower.getCreatedAt().isAfter(followee.getCreatedAt()) ? follower.getCreatedAt().plusSeconds(100): followee.getCreatedAt().plusSeconds(100);

        Follow follow = Follow.builder()
            .followerId(follower.getId())
            .followeeId(followee.getId())
            .createdAt(followCreatedAt)
            .build();

        followRepository.save(follow);

        return follow;
    }
}
