package clone.twitter.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
import clone.twitter.dto.request.UserFollowRequestDto;
import clone.twitter.dto.response.UserFollowResponseDto;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
public class FollowControllerTest extends BaseControllerTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    static final int NUMBER_OF_USERS = 20;

    static final int BEGINNING_INDEX_OF_STREAM_RANGE = 0;

    static final LocalDateTime BASE_CREATED_AT = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

    @Test
    @DisplayName("POST /users/{userId}/profile/follow - 특정 유저를 팔로우")
    void follow() throws Exception {
        // given
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, 2)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // when & then
        this.mockMvc.perform(post("/users/{userId}/profile/follow/{followerId}", users.get(1).getId(), users.get(0).getId())
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
            .andDo(document("post-follow",
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
                    fieldWithPath("_links.profile.href").description("link to docs profile")
                )
            ));
    }

    @Test
    @DisplayName("DELETE /users/{userId}/profile/follow/{followerId} - 특정 유저를 언팔로우")
    void unfollow() throws Exception {
        // given
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, 2)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        Follow follow = this.generateFollow(users.get(0), users.get(1));

        // when & then
        this.mockMvc.perform(delete("/users/{userId}/profile/follow/{followerId}", users.get(1).getId(), users.get(0).getId())
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
            .andDo(document("delete-follow",
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
                    fieldWithPath("_links.profile.href").description("link to docs profile")
                )
            ));
    }

    @Test
    @DisplayName("POST /users/{userId}/profile/follows - 특정유저가 자신이 팔로우하고 있는 팔로잉 유저목록 최초 요청")
    void postInitialFollowingList() throws Exception {
        // given
        // 유저 계정 20개 생성
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // 타겟유저(user1) 지정
        User user = users.get(BEGINNING_INDEX_OF_STREAM_RANGE);

        // 특정 유저(user1) -> userId의 끝자리 수가 2의 배수인 모든 유저 팔로우, userId의 끝자리 수가 3의 배수인 모든 유저 -> 특정 유저(user1) 팔로우, 맞팔로우 성립 userId 끝자리 수가 6의 배수.
        List<Follow> follows = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .filter(i -> !user.getId().equals(users.get(i).getId()))
            .mapToObj(i -> {
                List<Follow> followList = new ArrayList<>();
                if ((i + 1) % 2 == 0) {
                    followList.add(this.generateFollow(user, users.get(i)));
                }
                if ((i + 1) % 3 == 0) {
                    followList.add(this.generateFollow(users.get(i), user));
                }
                return followList;
            })
            .flatMap(List::stream)  // Flatten the stream
            .toList();

        // 특정유저가 자신이 팔로우하고 있는 팔로잉 유저목록 최초 조회시 사용하는 UserFollowRequestDto 객체 생성
        UserFollowRequestDto initialuserFollowRequestDto = UserFollowRequestDto.builder()
            .followerId(user.getId())
            .build();

        // when & then
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/users/{userId}/profile/follows", user.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(initialuserFollowRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].userResponseDto.userId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].userResponseDto.username").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].userResponseDto.profileName").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].userResponseDto.createdDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].follow.followerId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].follow.followeeId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].follow.createdAt").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*].isFollowing").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*]._links.target-user-profile-page.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userFollowResponseDtoList[*]._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.user-profile-page.href").isNotEmpty())
            .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
            .andDo(document("post-initial-following-list",
                links(
                    linkWithRel("user-profile-page").description("link to the profile page of the source user to which the follow list belongs"),
                    linkWithRel("profile").description("link to the api documentation profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("createdAtOfUserLastOnList").description("created date-time of follow of the user last on the follow list")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.userId").description("identifier of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.username").description("username of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.profileName").description("profile name of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.createdDate").description("created date of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.createdAt").description("created date-time of the follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].isFollowing").description("whether the target user of follow is following the source user of follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.target-user-profile-page.href").description("link to the profile page of the target user of the follow list"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.profile.href").description("link to the api documentation profile"),
                    fieldWithPath("_links.user-profile-page.href").description("link to the profile page of the source user to which the follow list belongs"),
                    fieldWithPath("_links.profile.href").description("link to the api documentation profile")
                )
            ));

    }

    @Test
    @DisplayName("POST /users/{userId}/profile/follows - 특정유저가 자신이 팔로우하고 있는 팔로잉 유저목록 추가 요청")
    void postMoreFollowingList() throws Exception {
        // given
        // 유저 계정 20개 생성
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // 타겟유저(user1) 지정
        User user = users.get(BEGINNING_INDEX_OF_STREAM_RANGE);

        // 특정 유저(user1) -> userId의 끝자리 수가 2의 배수인 모든 유저 팔로우, userId의 끝자리 수가 3의 배수인 모든 유저 -> 특정 유저(user1) 팔로우, 맞팔로우 성립 userId 끝자리 수가 6의 배수.
        List<Follow> follows = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .filter(i -> !user.getId().equals(users.get(i).getId()))
            .mapToObj(i -> {
                List<Follow> followList = new ArrayList<>();
                if ((i + 1) % 2 == 0) {
                    followList.add(this.generateFollow(user, users.get(i)));
                }
                if ((i + 1) % 3 == 0) {
                    followList.add(this.generateFollow(users.get(i), user));
                }
                return followList;
            })
            .flatMap(List::stream)  // Flatten the stream
            .toList();

        // 특정유저가 자신이 팔로우하고 있는 팔로잉 유저목록 최초 조회시 사용하는 UserFollowRequestDto 객체 생성
        UserFollowRequestDto initialuserFollowRequestDto = UserFollowRequestDto.builder()
            .followerId(user.getId())
            .build();

        // 특정유저가 자신이 팔로우하고 있는 팔로잉 유저목록 최초 조회 후 결과의 마지막에 있는 유저의 팔로우 생성시간 정보(createdAtOfUserLastOnList) 추출
        MvcResult result = mockMvc.perform(post("/users/{userId}/profile/follows", user.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(initialuserFollowRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        HalWrapper<UserFollowResponseModel> halWrapper = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<HalWrapper<UserFollowResponseModel>>() {});

        CollectionModel<UserFollowResponseModel> collectionModel = CollectionModel.of(halWrapper.get_embedded().getUserFollowResponseDtoList());

        assert collectionModel != null;

        List<UserFollowResponseModel> userFollowResponseModelList = new ArrayList<>(collectionModel.getContent());

        UserFollowResponseModel lastUserFollowResponseModel = userFollowResponseModelList.get(userFollowResponseModelList.size() - 1);

        UserFollowResponseDto lastUserFollowResponseDto = lastUserFollowResponseModel.getContent();

        assert lastUserFollowResponseDto != null;

        // 특정유저가 자신이 팔로우하고 있는 팔로잉 유저목록 추가 조회시 사용하는 UserFollowRequestDto 객체 생성
        UserFollowRequestDto moreUserFollowRequestDto = UserFollowRequestDto.builder()
            .followerId(user.getId())
            .createdAtOfUserLastOnList(lastUserFollowResponseDto.getFollow().getCreatedAt())
            .build();

        // when & then
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/users/{userId}/profile/follows", user.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(moreUserFollowRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andDo(document("post-more-following-list",
                links(
                    linkWithRel("user-profile-page").description("link to the profile page of the source user to which the follow list belongs"),
                    linkWithRel("profile").description("link to the api documentation profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("createdAtOfUserLastOnList").description("created date-time of follow of the user last on the follow list")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.userId").description("identifier of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.username").description("username of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.profileName").description("profile name of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.createdDate").description("created date of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.createdAt").description("created date-time of the follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].isFollowing").description("whether the target user of follow is following the source user of follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.target-user-profile-page.href").description("link to the profile page of the target user of the follow list"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.profile.href").description("link to the api documentation profile"),
                    fieldWithPath("_links.user-profile-page.href").description("link to the profile page of the source user to which the follow list belongs"),
                    fieldWithPath("_links.profile.href").description("link to the api documentation profile")
                )
            ));
    }

    @Test
    @DisplayName("POST /users/{userId}/profile/follows - 특정유저가 자신을 팔로우하고 있는 팔로워 유저목록 최초 요청")
    void postInitialFollowerList() throws Exception {
        // given
        // 유저 계정 20개 생성
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // 타겟유저(user1) 지정
        User user = users.get(BEGINNING_INDEX_OF_STREAM_RANGE);

        // 특정 유저(user1) -> userId의 끝자리 수가 2의 배수인 모든 유저 팔로우, userId의 끝자리 수가 3의 배수인 모든 유저 -> 특정 유저(user1) 팔로우, 맞팔로우 성립 userId 끝자리 수가 6의 배수.
        List<Follow> follows = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .filter(i -> !user.getId().equals(users.get(i).getId()))
            .mapToObj(i -> {
                List<Follow> followList = new ArrayList<>();
                if ((i + 1) % 2 == 0) {
                    followList.add(this.generateFollow(user, users.get(i)));
                }
                if ((i + 1) % 3 == 0) {
                    followList.add(this.generateFollow(users.get(i), user));
                }
                return followList;
            })
            .flatMap(List::stream)  // Flatten the stream
            .toList();

        // 특정유저가 자신을 팔로우하고 있는 팔로워 유저목록 최초 조회시 사용하는 UserFollowRequestDto 객체 생성
        UserFollowRequestDto initialuserFollowRequestDto = UserFollowRequestDto.builder()
            .followerId(user.getId())
            .build();

        // when & then
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/users/{userId}/profile/follows", user.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(initialuserFollowRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andDo(document("post-initial-follower-list",
                links(
                    linkWithRel("user-profile-page").description("link to the profile page of the source user to which the follow list belongs"),
                    linkWithRel("profile").description("link to the api documentation profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("createdAtOfUserLastOnList").description("created date-time of follow of the user last on the follow list")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.userId").description("identifier of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.username").description("username of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.profileName").description("profile name of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.createdDate").description("created date of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.createdAt").description("created date-time of the follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].isFollowing").description("whether the target user of follow is following the source user of follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.target-user-profile-page.href").description("link to the profile page of the target user of the follow list"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.profile.href").description("link to the api documentation profile"),
                    fieldWithPath("_links.user-profile-page.href").description("link to the profile page of the source user to which the follow list belongs"),
                    fieldWithPath("_links.profile.href").description("link to the api documentation profile")
                )
            ));
    }

    @Test
    @DisplayName("POST /users/{userId}/profile/follows - 특정유저가 자신을 팔로우하고 있는 팔로워 유저목록 추가 요청")
    void postMoreFollowerList() throws Exception {
        // given
        // 유저 계정 20개 생성
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // 타겟유저(user1) 지정
        User user = users.get(BEGINNING_INDEX_OF_STREAM_RANGE);

        // 특정 유저(user1) -> userId의 끝자리 수가 2의 배수인 모든 유저 팔로우, userId의 끝자리 수가 3의 배수인 모든 유저 -> 특정 유저(user1) 팔로우, 맞팔로우 성립 userId 끝자리 수가 6의 배수.
        List<Follow> follows = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .filter(i -> !user.getId().equals(users.get(i).getId()))
            .mapToObj(i -> {
                List<Follow> followList = new ArrayList<>();
                if ((i + 1) % 2 == 0) {
                    followList.add(this.generateFollow(user, users.get(i)));
                }
                if ((i + 1) % 3 == 0) {
                    followList.add(this.generateFollow(users.get(i), user));
                }
                return followList;
            })
            .flatMap(List::stream)  // Flatten the stream
            .toList();

        // 특정유저가 자신을 팔로우하고 있는 팔로워 유저목록 최초 조회시 사용하는 UserFollowRequestDto 객체 생성
        UserFollowRequestDto initialuserFollowRequestDto = UserFollowRequestDto.builder()
            .followerId(user.getId())
            .build();

        // 특정유저가 자신을 팔로우하고 있는 팔로워 유저목록 최초 조회 후 결과의 마지막에 있는 유저의 팔로우 생성시간 정보(createdAtOfUserLastOnList) 추출
        MvcResult result = mockMvc.perform(post("/users/{userId}/profile/follows", user.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(initialuserFollowRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        HalWrapper<UserFollowResponseModel> halWrapper = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<HalWrapper<UserFollowResponseModel>>() {});

        CollectionModel<UserFollowResponseModel> collectionModel = CollectionModel.of(halWrapper.get_embedded().getUserFollowResponseDtoList());

        assert collectionModel != null;

        List<UserFollowResponseModel> userFollowResponseModelList = new ArrayList<>(collectionModel.getContent());

        UserFollowResponseModel lastUserFollowResponseModel = userFollowResponseModelList.get(userFollowResponseModelList.size() - 1);

        UserFollowResponseDto lastUserFollowResponseDto = lastUserFollowResponseModel.getContent();

        assert lastUserFollowResponseDto != null;

        // 특정유저가 자신을 팔로우하고 있는 팔로워 유저목록 추가 조회시 사용하는 UserFollowRequestDto 객체 생성
        UserFollowRequestDto moreUserFollowRequestDto = UserFollowRequestDto.builder()
            .followerId(user.getId())
            .createdAtOfUserLastOnList(lastUserFollowResponseDto.getFollow().getCreatedAt())
            .build();

        // when & then
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/users/{userId}/profile/follows", user.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(moreUserFollowRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].userId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].username").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].profileName").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].createdDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*]._links.user-profile.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*]._links.timeline.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*]._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.self.href").isNotEmpty())
            .andExpect(jsonPath("$._links.more-users-liked-tweet.href").isNotEmpty())
            .andExpect(jsonPath("$._links.tweet.href").isNotEmpty())
            .andDo(document("post-more-follower-list",
                links(
                    linkWithRel("user-profile-page").description("link to the profile page of the source user to which the follow list belongs"),
                    linkWithRel("profile").description("link to the api documentation profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("createdAtOfUserLastOnList").description("created date-time of follow of the user last on the follow list")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.userId").description("identifier of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.username").description("username of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.profileName").description("profile name of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].userResponseDto.createdDate").description("created date of user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followerId").description("identifier of source user account following the target user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.followeeId").description("identifier of target user account being followed by the source user account"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].follow.createdAt").description("created date-time of the follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*].isFollowing").description("whether the target user of follow is following the source user of follow"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.target-user-profile-page.href").description("link to the profile page of the target user of the follow list"),
                    fieldWithPath("_embedded.userFollowResponseDtoList[*]._links.profile.href").description("link to the api documentation profile"),
                    fieldWithPath("_links.user-profile-page.href").description("link to the profile page of the source user to which the follow list belongs"),
                    fieldWithPath("_links.profile.href").description("link to the api documentation profile")
                )
            ));
    }

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
