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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.RestDocsConfiguration;
import clone.twitter.domain.Follow;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.dto.request.TweetLoadRequestDto;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@ActiveProfiles("test")
@Import(RestDocsConfiguration.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@SpringBootTest
class TweetControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionStatus status;

    /**
     * tweet table user_id 필드의 foreign key constraint 사유로 더미 user 객체 각 테스트 메서드 실행 전 생성. timeline 조회 시 팔로우 관계가 전제되어야 하므로 테스트 메서드 실행 전 더미 유저간 팔로우 생성.
     */
    @BeforeEach
    void saveDummyUser() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // Harry, Ronny 더미 유저 생성
        User user1Harry = new User("idOfHarry", "harry", "harry@gmail.com", "AAAAA", "harry profile", LocalDate.of(1991, 1, 1), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        User user2Ronny = new User("idOfRonny", "ronny", "ronny@gmail.com", "BBBBBB", "ronny profile", LocalDate.of(1992, 2, 2), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        userRepository.save(user1Harry);
        userRepository.save(user2Ronny);

        // Ronny -> Harry 팔로우 생성
        Follow follow1 = new Follow(user2Ronny.getId(), user1Harry.getId());

        followRepository.follow(follow1);
    }

    @AfterEach
    void rollBack() {
        transactionManager.rollback(status);
    }

    @Test
    @DisplayName("POST /tweets - 정상적인 트윗 포스팅 케이스")
    void composeTweet() throws Exception {
        // given
        TweetComposeRequestDto tweetComposeDto = TweetComposeRequestDto.builder()
            .text("hello, this is my first tweet.")
            .userId("idOfHarry")
            .build();

        // when & then
        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweetComposeDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("id").isNotEmpty())
            .andExpect(jsonPath("createdAt").isNotEmpty())
            .andDo(document("compose-tweet",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("timeline-tweets").description("link to existing list of timeline tweets"),
                    linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("text").description("content of new tweet"),
                    fieldWithPath("userId").description("id of user who composed tweet")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("url of newly created content"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("id").description("identifier of new tweet"),
                    fieldWithPath("text").description("content of new tweet"),
                    fieldWithPath("userId").description("id of user who composed tweet"),
                    fieldWithPath("createdAt").description("local date time of when the tweet is created"),
                    fieldWithPath("_links.self.href").description("link to self"),
                    fieldWithPath("_links.timeline-tweets.href").description("link to tweet list"),
                    fieldWithPath("_links.profile.href").description("link to profile")
                )
            ));
    }

    /**
     * postTweetExcessiveInput() 테스트 메서드와 테스트 병행 불가. application.yml에서 spring.jackson.deserialization.fail-on-unknown-properties 설정 조정 필요.
     * @see #composeTweetExcessiveInput()
     */
    @Test
    @DisplayName("POST /tweets - 트윗에 받기로한 필드 외 불명의 더미 필드(properties) 데이터가 같이 들어올 경우 bad request로 응답")
    void composeTweetBadRequest() throws Exception {
        // given
        Tweet tweet = Tweet.builder()
            .id("1")
            .text("hello, this is my first tweet.")
            .userId("idOfHarry")
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        // when & then
        // 원래 postTweetExcessiveInput() 메서드와 결과 동일(201 반환) -> springboot가 제공하는 properties를 활용한 object mapper 확장기능 사용(401 반환)
        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweet)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tweets - 트윗에 받기로한 필드의 종류와 갯수가 일치하나 값이 비어있는 경우 bad request로 응답")
    void composeTweetBadRequestEmptyInput() throws Exception {
        // given
        TweetComposeRequestDto tweetComposeDto = TweetComposeRequestDto.builder().build();

        // when & then
        this.mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(tweetComposeDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /tweets/timeline - 타임라인 트윗을 n개씩 작성시간의 내림차순으로 최초 조회")
    void getInitialTweets() throws Exception {
        // given
        final String userIdOfHarry = "idOfHarry";

        final String userIdOfRonny = "idOfRonny";

        final int numberOfTweetsComposed = 15;

        LocalDateTime baseCreatedAt = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

        // Harry, 트윗 15개 작성(Ronny -> Harry 팔로우 중)
        IntStream.range(0, numberOfTweetsComposed).forEach(i -> {
            this.generateTweet(i, userIdOfHarry, baseCreatedAt);
        });

        // when & then
        // Harry를 팔로우 중인 Ronny의 타임라인 조회
        this.mockMvc.perform(get("/tweets/timeline")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .param("userId", userIdOfRonny))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$._embedded.tweetList").exists())
            .andExpect(jsonPath("$._embedded.tweetList[*].id").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*].text").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*].userId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*].createdAt").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*]._links.self.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*]._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.profile.href").exists())
            .andDo(document("get-initial-tweets",
                links(
                    linkWithRel("self").description("link to the tweets list"),
                    linkWithRel("profile").description("Link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                queryParameters(
                    parameterWithName("userId").description("user id of user browsing tweet timeline")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.tweetList[]").description("List of tweets"),
                    fieldWithPath("_embedded.tweetList[].id").description("The id of the tweet"),
                    fieldWithPath("_embedded.tweetList[].text").description("The text content of the tweet"),
                    fieldWithPath("_embedded.tweetList[].userId").description("The id of the user who composed the tweet"),
                    fieldWithPath("_embedded.tweetList[].createdAt").description("The creation time of the tweet"),
                    fieldWithPath("_embedded.tweetList[]._links.self.href").description("Link to the tweet"),
                    fieldWithPath("_embedded.tweetList[]._links.profile.href").description("Link to profile"),
                    fieldWithPath("_links.self.href").description("Link to the tweets list"),
                    fieldWithPath("_links.profile.href").description("Link to profile")
                )
            ));
    }

    @Test
    @DisplayName("GET /tweets/timeline/next - 타임라인 트윗을 n개씩 작성시간의 내림차순으로 추가 조회")
    void getNextTweets() throws Exception {
        // given
        final String userIdOfHarry = "idOfHarry";

        final String userIdOfRonny = "idOfRonny";

        final int numberOfTweetsComposed = 15;

        LocalDateTime baseCreatedAt = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

        // Harry, 트윗 15개 작성(Ronny -> Harry 팔로우 중)
        IntStream.range(0, numberOfTweetsComposed).forEach(i -> {
            this.generateTweet(i, userIdOfHarry, baseCreatedAt);
        });

        TweetLoadRequestDto tweetComposeDto = TweetLoadRequestDto.builder()
            .userIdOfViewer(userIdOfRonny)
            .createdAtOfLastViewedTweet(baseCreatedAt.plusSeconds(numberOfTweetsComposed - tweetRepository.TWEET_LOAD_LIMIT))
            .build();

        // when & then
        // Harry를 팔로우 중인 Ronny의 타임라인 조회
        this.mockMvc.perform(get("/tweets/timeline/next")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweetComposeDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$._embedded.tweetList").exists())
            .andExpect(jsonPath("$._embedded.tweetList[*].id").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*].text").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*].userId").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*].createdAt").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*]._links.self.href").isNotEmpty())
            .andExpect(jsonPath("$._embedded.tweetList[*]._links.profile.href").isNotEmpty())
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.profile.href").exists())
            .andDo(document("get-next-tweets",
                links(
                    linkWithRel("self").description("link to the tweets list"),
                    linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                    fieldWithPath("userIdOfViewer").description("id of user who composed tweet"),
                    fieldWithPath("createdAtOfLastViewedTweet").description("created datetime of last tweet in the timeline list")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_embedded.tweetList[]").description("List of tweets"),
                    fieldWithPath("_embedded.tweetList[].id").description("The id of the tweet"),
                    fieldWithPath("_embedded.tweetList[].text").description("The text content of the tweet"),
                    fieldWithPath("_embedded.tweetList[].userId").description("The id of the user who composed the tweet"),
                    fieldWithPath("_embedded.tweetList[].createdAt").description("The creation time of the tweet"),
                    fieldWithPath("_embedded.tweetList[]._links.self.href").description("Link to the tweet"),
                    fieldWithPath("_embedded.tweetList[]._links.profile.href").description("Link to profile"),
                    fieldWithPath("_links.self.href").description("Link to the timeline tweets list"),
                    fieldWithPath("_links.profile.href").description("Link to profile")
                )
            ));
    }

    @Test
    @DisplayName("GET /tweets/{tweetId} - 기존 특정 트윗 하나 정상 조회, status 200 응답")
    void getTweet() throws Exception {
        // given
        final String userIdOfHarry = "idOfHarry";

        LocalDateTime baseCreatedAt = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

        Tweet tweet = this.generateTweet(1, userIdOfHarry, baseCreatedAt);

        // when & then
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/tweets/{tweetId}", tweet.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("text").exists())
            .andExpect(jsonPath("userId").exists())
            .andExpect(jsonPath("createdAt").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get-tweet",
                links(
                    linkWithRel("self").description("link to the tweet"),
                    linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of tweet")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("id").description("identifier of new tweet"),
                    fieldWithPath("text").description("content of new tweet"),
                    fieldWithPath("userId").description("id of tweet composer"),
                    fieldWithPath("createdAt").description("local date time of when the tweet is created"),
                    fieldWithPath("_links.self.href").description("link to self"),
                    fieldWithPath("_links.profile.href").description("link to profile")
                )
            ));
    }

    @Test
    @DisplayName("GET /tweets/{tweetId} - 존재하지 않는 트윗 조회 요청시 status 404 응답")
    void getTweet404() throws Exception {
        // when & then
        this.mockMvc.perform(get("/tweets/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /tweets/{tweetId} - 기존 특정 트윗 하나 정상 삭제, status 204 응답")
    void deleteTweet() throws Exception {
        // given
        final String userIdOfHarry = "idOfHarry";

        LocalDateTime baseCreatedAt = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

        Tweet tweet = this.generateTweet(1, userIdOfHarry, baseCreatedAt);

        // when & then
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/tweets/{tweetId}", tweet.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.index").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("delete-tweet",
                links(
                    linkWithRel("index").description("Link to index page"),
                    linkWithRel("profile").description("Link to profile")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                pathParameters(
                    parameterWithName("tweetId").description("identifier of tweet")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of content(HAL-Json)")
                ),
                responseFields(
                    fieldWithPath("_links.index.href").description("Link to index page"),
                    fieldWithPath("_links.profile.href").description("Link to profile")
                )
            ));
    }

    @Test
    @DisplayName("DELETE /tweets/{tweetId} - 존재하지 않는 트윗 삭제 요청, status 404 응답")
    void deleteTweet404() throws Exception {
        // when & then
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/tweets/{tweetId}", "id999999")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    private Tweet generateTweet(int index, String composerId, LocalDateTime baseCreatedAt) {
        Tweet tweet = Tweet.builder()
            .id(UUID.randomUUID().toString())
            .text("tweet text " + (index + 1))
            .userId(composerId)
            .createdAt(baseCreatedAt.plusSeconds(index))
            .build();

        return this.tweetRepository.save(tweet);
    }

    /**
     * 테스트 메서드 생략 사유: postTweetBadRequest() 테스트 메서드와 테스트와 병행 불가. application.yml에서 spring.jackson.deserialization.fail-on-unknown-properties 설정 조정 필요.
     * @see #composeTweetBadRequest()
     */
    @Test
    @DisplayName("[생략]POST /tweets - 트윗에 받기로한 필드 외 불명의 더미 필드(properties)와 데이터가 같이 들어올 경우 받기로 한 값 외 무시하고 정상처리")
    void composeTweetExcessiveInput() throws Exception {
//        // when & then
//        Tweet tweet = Tweet.builder()
//            .id("1")
//            .text("hello, this is my first tweet.")
//            .userId("idOfHarry")
//            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
//            .build();
//
//        // when & then
//        mockMvc.perform(post("/tweets")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaTypes.HAL_JSON)
//                .content(objectMapper.writeValueAsString(tweet)))
//            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
//            .andExpect(status().isCreated()) // 201이라고 직접 입력하는 것보다 type-safe
//            .andExpect(jsonPath("id").exists())
//            .andExpect(header().exists(HttpHeaders.LOCATION))
//            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
//            // 아래 코드: tweet의 id, createdAt 필드들은 상관없는(TweetComposeRequestDto의 필드에 없는) 더미 데이터가 요청으로 들어와도 무시되어야 한다
//            .andExpect(jsonPath("id").value(Matchers.not("1")))
//            .andExpect(jsonPath("createdAt").value(Matchers.not(LocalDateTime.of(2023, 6, 1, 1, 1, 1))));
    }

    /**
     * <ul>
     *     <li>테스트 메서드 생략 사유: 현재 비즈니스 로직상 해당사항 없음. 해당 시 별도 validator 클래스 규정(Errors.rejectValue()...) 후 @Component로 빈 등록하여 test 가능.</li>
     *     <ul>
     *         <li>해당 경우 eg.1. 실제 포스팅을 한 userId가 아닌 이상한 유저의 id가 들어오는 경우(유저인증 파트에서 해결 예정)</li>
     *         <li>해당 경우 eg.2. 트윗내 투표기능의 기간 설정 시 시작날짜가 종료날짜보다 늦는 경우 등</li>
     *     </ul>
     * </ul>
     * @see clone.twitter.controller.TweetValidator
     * @see clone.twitter.common.ErrorSerializer
     */
    @Test
    @DisplayName("[생략]POST /tweets - 트윗에 받기로한 필드의 종류의 갯수가 일치하고 값이 있으나, 해당 값이 비즈니스 로직상 이상한 경우")
    void composeTweetBadRequestWrongInput() throws Exception {
//        // given
//        TweetComposeRequestDto tweetComposeDto = TweetComposeRequestDto.builder()
//            .text("hello, this is my first tweet.")
//            .userId("strangeUserId")
//            .build();
//
//        // when & then
//        this.mockMvc.perform(post("/tweets")
//            .contentType(MediaType.APPLICATION_JSON_VALUE)
//            .content(this.objectMapper.writeValueAsString(tweetComposeDto)))
//            .andDo(print())
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("content[0].objectName").exists())
//            .andExpect(jsonPath("content[0].defaultMessage").exists())
//            .andExpect(jsonPath("content[0].code").exists())
//            .andExpect(jsonPath("_links.index").exists());
    }
}