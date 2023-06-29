package clone.twitter.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.BaseControllerTest;
import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.repository.LikeTweetRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class LikeTweetControllerTest extends BaseControllerTest {
    @Autowired
    LikeTweetRepository likeTweetRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    LikeTweetController likeTweetController;

    static final int NUMBER_OF_USERS = 10;

    static final int NUMBER_OF_LIKE_TWEETS = 10;

    static final int BEGINNING_INDEX_OF_STREAM_RANGE = 0;

    static final LocalDateTime BASE_CREATED_AT = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

    @Test
    @DisplayName("POST /tweets/{tweetId}/like/users/{userId} - 좋아요 요청")
    void postLikeTweet() throws Exception {
        // given
        User user = this.generateUser(BEGINNING_INDEX_OF_STREAM_RANGE, BASE_CREATED_AT);

        Tweet tweet = this.generateTweet(BEGINNING_INDEX_OF_STREAM_RANGE, user.getId(), BASE_CREATED_AT);

        // when & then
        this.mockMvc.perform(post("/tweets/{tweetId}/like/users/{userId}", tweet.getId(), user.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("like-tweet",
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of individual tweet"),
                    parameterWithName("userId").description("identifier of user who composed tweet")
                )
            ));
    }

    @Test
    @DisplayName("DELETE /tweets/{tweetId}/like/users/{userId} - 좋아요 취소 요청")
    void deleteLikeTweet() throws Exception {
        // given
        User user = this.generateUser(BEGINNING_INDEX_OF_STREAM_RANGE, BASE_CREATED_AT);

        Tweet tweet = this.generateTweet(BEGINNING_INDEX_OF_STREAM_RANGE, user.getId(), BASE_CREATED_AT);

        this.generateLikeTweet(BEGINNING_INDEX_OF_STREAM_RANGE, user.getId(), tweet.getId(), BASE_CREATED_AT);


        // when & then
        this.mockMvc.perform(delete("/tweets/{tweetId}/like/users/{userId}", tweet.getId(), user.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andDo(document("unlike-tweet",
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of individual tweet"),
                    parameterWithName("userId").description("identifier of user who composed tweet")
                )
            ));
    }

    @Test
    @DisplayName("GET /tweets/{tweetId}/like/users - 좋아요 표시 유저목록 조회 최초 요청")
    void getUsersLikedTweet() throws Exception {
        // given
        // 10개의 유저 계정 생성
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // 첫 번째 유저가 1개의 트윗 생성
        Tweet tweet = this.generateTweet(BEGINNING_INDEX_OF_STREAM_RANGE, users.get(BEGINNING_INDEX_OF_STREAM_RANGE).getId(), BASE_CREATED_AT);

        // 작성자 포함 모든 유저 10명이 트윗 1개에 좋아요 표시
        List<LikeTweet> likeTweets = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE,
                NUMBER_OF_LIKE_TWEETS)
            .mapToObj(i -> this.generateLikeTweet(i, users.get(i).getId(), tweet.getId(), BASE_CREATED_AT))
            .toList();

        // when & then
        this.mockMvc.perform(get("/tweets/{tweetId}/like/users", tweet.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$.['_embedded'].userResponseDtoList.length()").value(3))
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
            .andDo(document("get-users-who-liked-tweet",
                links(
                    linkWithRel("profile").description("documentation link to the api profile"),
                    linkWithRel("self").description("link to self(load initial users who liked the tweet)"),
                    linkWithRel("more-users-liked-tweet").description("link to more users who liked the tweet"),
                    linkWithRel("tweet").description("link to the tweet")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of individual tweet")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.userResponseDtoList[*].userId").description("identifier of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*].username").description("username of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*].profileName").description("profile name of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*].createdDate").description("created date of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*]._links.user-profile.href").description("link to user profile page"),
                    fieldWithPath("_embedded.userResponseDtoList[*]._links.timeline.href").description("link to timeline tweet list"),
                    fieldWithPath("_embedded.userResponseDtoList[*]._links.profile.href").description("documentation link to the user info api profile"),
                    fieldWithPath("_links.profile.href").description("documentation link to the api profile"),
                    fieldWithPath("_links.self.href").description("link to self(load initial list of users who liked the tweet)"),
                    fieldWithPath("_links.more-users-liked-tweet.href").description("link to more list of users who liked the tweet"),
                    fieldWithPath("_links.tweet.href").description("link to the tweet")
                )
            ));
    }

    @Test
    @DisplayName("GET /tweets/{tweetId}/like/users/{userIdLastOnList} - 좋아요 표시 유저목록 조회 추가 요청")
    void getMoreUsersLikedTweet() throws Exception {
        // given
        // 10개의 유저 계정 생성
        List<User> users = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE, NUMBER_OF_USERS)
            .mapToObj(i -> this.generateUser(i, BASE_CREATED_AT))
            .toList();

        // 첫 번째 유저가 1개의 트윗 생성
        Tweet tweet = this.generateTweet(BEGINNING_INDEX_OF_STREAM_RANGE, users.get(BEGINNING_INDEX_OF_STREAM_RANGE).getId(), BASE_CREATED_AT);

        // 작성자 포함 모든 유저 10명이 트윗 1개에 좋아요 생성
        List<LikeTweet> likeTweets = IntStream.range(BEGINNING_INDEX_OF_STREAM_RANGE,
                NUMBER_OF_LIKE_TWEETS)
            .mapToObj(i -> this.generateLikeTweet(i, users.get(i).getId(), tweet.getId(), BASE_CREATED_AT))
            .toList();

        // 좋아요를 표시한 유저목록 최초 조회 요청시 받아온 유저목록의 마지막에 있는 유저 id 추출
        ResponseEntity<CollectionModel<UserResponseModel>> usersLikedTweetResponseEntity = likeTweetController.getUsersLikedTweet(tweet.getId());

        CollectionModel<UserResponseModel> collectionModel = usersLikedTweetResponseEntity.getBody();

        assert collectionModel != null;

        List<UserResponseModel> userResponseModelList = new ArrayList<>(collectionModel.getContent());

        UserResponseModel lastUserResponseModel = userResponseModelList.get(userResponseModelList.size() - 1);

        UserResponseDto lastUserResponseDto = lastUserResponseModel.getContent();

        assert lastUserResponseDto != null;

        // when & then
        this.mockMvc.perform(get("/tweets/{tweetId}/like/users/{userIdLastOnList}", tweet.getId(), lastUserResponseDto.getUserId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$.['_embedded'].userResponseDtoList.length()").value(3))
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].userId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].username").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].profileName").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*].createdDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*]._links.user-profile.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*]._links.timeline.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.userResponseDtoList[*]._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.tweet.href").isNotEmpty())
            .andDo(document("get-more-users-who-liked-tweet",
                links(
                    linkWithRel("profile").description("documentation link to the api profile"),
                    linkWithRel("tweet").description("link to the tweet")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of individual tweet"),
                    parameterWithName("userIdLastOnList").description("identifier of the last user of previously loaded tweet list")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.userResponseDtoList[*].userId").description("identifier of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*].username").description("username of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*].profileName").description("profile name of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*].createdDate").description("created date of user account"),
                    fieldWithPath("_embedded.userResponseDtoList[*]._links.user-profile.href").description("link to user profile page"),
                    fieldWithPath("_embedded.userResponseDtoList[*]._links.timeline.href").description("link to timeline tweet list"),
                    fieldWithPath("_embedded.userResponseDtoList[*]._links.profile.href").description("documentation link to the user info api profile"),
                    fieldWithPath("_links.profile.href").description("documentation link to the api profile"),
                    fieldWithPath("_links.tweet.href").description("link to the tweet")
                )
            ));;
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

    private Tweet generateTweet(int index, String composerId, LocalDateTime baseCreatedAt) {
        Tweet tweet = Tweet.builder()
            .id("tweetId" + (index + 1))
            .text("tweet text " + (index + 1))
            .userId(composerId)
            .createdAt(baseCreatedAt.plusSeconds(index + 1))
            .build();

        return tweetRepository.save(tweet);
    }

    private LikeTweet generateLikeTweet(int index, String userId, String tweetId, LocalDateTime tweetCreatedAt) {
        LikeTweet likeTweet = LikeTweet.builder()
            .userId("user" + (index + 1))
            .tweetId(tweetId) // fixed target of like-tweet
            .createdAt(tweetCreatedAt.plusSeconds(index + 1))
            .build();

        likeTweetRepository.save(likeTweet);

        return likeTweet;
    }
}
