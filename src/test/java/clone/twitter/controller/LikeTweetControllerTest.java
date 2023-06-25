package clone.twitter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import clone.twitter.repository.LikeTweetRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class LikeTweetControllerTest extends BaseControllerTest {
    @Autowired
    LikeTweetRepository likeTweetRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetRepository tweetRepository;

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
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("tweetId").isNotEmpty())
            .andExpect(jsonPath("isLikedByUser").isNotEmpty())
            .andExpect(jsonPath("$._links.tweet.href").isNotEmpty())
            .andExpect(jsonPath("$._links.timeline.href").isNotEmpty())
            .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
            .andDo(document("unlike-tweet",
                links(
                    linkWithRel("tweet").description("link to individual tweet"),
                    linkWithRel("timeline").description("link to existing list of timeline tweets"),
                    linkWithRel("profile").description("documentation link to the api profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of individual tweet"),
                    parameterWithName("userId").description("identifier of user who composed tweet")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("tweetId").description("identifier of new tweet"),
                    fieldWithPath("isLikedByUser").description("status whether the tweet is liked by the user or not"),
                    fieldWithPath("_links.tweet.href").description("link to individual tweet"),
                    fieldWithPath("_links.timeline.href").description("link to timeline tweet list"),
                    fieldWithPath("_links.profile.href").description("documentation link to the api profile")
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
