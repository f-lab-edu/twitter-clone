package clone.twitter.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.common.RestDocsConfiguration;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.WebApplicationContext;

//@ExtendWith(RestDocumentationExtension.class) // according to Spring Rest Docs official documentation
@Import(RestDocsConfiguration.class) // for http body json formatting customization
@AutoConfigureMockMvc // according to lecture reference
@AutoConfigureRestDocs // according to lecture reference. Junit5에서는 작동하지 않는다?
@Transactional
@SpringBootTest // according to lecture reference
class TweetControllerTest {
    /**
     * springboot를 사용중일 시 mapping jackre
     * son json이 의존성으로 설정돼있으면 ObjectMapper가 자동으로 bean으로 등록
     */
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionStatus status;

    /**
     * tweet table의 user_id 필드의 foreign key constraint으로 인해 더미 user 객체도 각 테스트 전에 생성 필요
     */
    @BeforeEach
    void saveDummyUser() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        User user = new User("idOfHarry", "harry", "harry@gmail.com", "b03b29", "hello", LocalDate.of(1999, 9, 9), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        userRepository.save(user);
    }

//    // code according to Spring Rest Docs official documentation
//    @BeforeEach
//    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//            .apply(documentationConfiguration(restDocumentation))
//            .build();
//    }

    @AfterEach
    void rollBack() {
        transactionManager.rollback(status);
    }

    @Test
    @DisplayName("POST /tweets - 정상적인 트윗 포스팅 케이스")
    void composeTweet() throws Exception {
        TweetComposeRequestDto tweetComposeDto = TweetComposeRequestDto.builder()
            .text("hello, this is my first tweet.")
            .userId("idOfHarry")
            .build();

        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweetComposeDto)))
            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
            .andExpect(status().isCreated()) // 201이라고 직접 입력하는 것보다 type-safe
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("id").isNotEmpty())
            .andExpect(jsonPath("createdAt").isNotEmpty())
            .andDo(document("compose-tweet",
                links(
                    linkWithRel("self").description("link to self"),
                    linkWithRel("tweets").description("link to existing list of tweets"),
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
                    fieldWithPath("_links.tweets.href").description("link to tweet list"),
                    fieldWithPath("_links.profile.href").description("link to profile")
                )
            ))
        ;
    }

//
////    /**
////     * 아래 postTweetBadRequest() 테스트와 병행 불가. application.yml에서 spring.jackson.deserialization.fail-on-unknown-properties 설정 조정 필요.
////     * @see #composeTweetBadRequest()
////     */
////    @Test
////    @DisplayName("POST /tweets - 트윗에 받기로한 필드 외 불명의 더미 필드(properties)와 데이터가 같이 들어올 경우 받기로 한 값 외 무시하고 정상처리")
////    void composeTweetExcessiveInput() throws Exception {
////        Tweet tweet = Tweet.builder()
////            .id("1")
////            .text("hello, this is my first tweet.")
////            .userId("idOfHarry")
////            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
////            .build();
////
////        mockMvc.perform(post("/tweets")
////                .contentType(MediaType.APPLICATION_JSON_VALUE)
////                .accept(MediaTypes.HAL_JSON)
////                .content(objectMapper.writeValueAsString(tweet)))
////            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
////            .andExpect(status().isCreated()) // 201이라고 직접 입력하는 것보다 type-safe
////            .andExpect(jsonPath("id").exists())
////            .andExpect(header().exists(HttpHeaders.LOCATION))
////            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
////            // 아래 코드: tweet의 id, createdAt 필드들은 상관없는(TweetComposeRequestDto의 필드에 없는) 더미 데이터가 요청으로 들어와도 무시되어야 한다
////            .andExpect(jsonPath("id").value(Matchers.not("1")))
////            .andExpect(jsonPath("createdAt").value(Matchers.not(LocalDateTime.of(2023, 6, 1, 1, 1, 1))));
////    }
//
//    /**
//     * 위 postTweetExcessiveInput()와 테스트 병행 불가. application.yml에서 spring.jackson.deserialization.fail-on-unknown-properties 설정 조정 필요.
//     * @see #composeTweetExcessiveInput()
//     */
//    @Test
//    @DisplayName("POST /tweets - 트윗에 받기로한 필드 외 불명의 더미 필드(properties) 데이터가 같이 들어올 경우 bad request로 응답")
//    void composeTweetBadRequest() throws Exception {
//        Tweet tweet = Tweet.builder()
//            .id("1")
//            .text("hello, this is my first tweet.")
//            .userId("idOfHarry")
//            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
//            .build();
//
//        // 원래대로면 결과는 이전 테스트(postTweetExcessiveInput)와 동일(201 반환) -> springboot가 제공하는 properties를 활용한 object mapper 확장기능 사용(401 반환)
//        mockMvc.perform(post("/tweets")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaTypes.HAL_JSON)
//                .content(objectMapper.writeValueAsString(tweet)))
//            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
//            .andExpect(status().isBadRequest()); // 400이라고 직접 입력하는 것보다 type-safe
//    }
//
//    @Test
//    @DisplayName("POST /tweets - 트윗에 받기로한 필드의 종류와 갯수가 일치하나 값이 비어있는 경우 bad request로 응답")
//    void composeTweetBadRequestEmptyInput() throws Exception {
//        TweetComposeRequestDto tweetComposeDto = TweetComposeRequestDto.builder().build();
//
//        this.mockMvc.perform(post("/tweets")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(this.objectMapper.writeValueAsString(tweetComposeDto)))
//            .andExpect(status().isBadRequest());
//    }
//
//    /**
//     * <ul>
//     *     <li>현재 해당사항 없음. 해당 시 별도 validator 클래스 규정(Errors.rejectValue()...) 후 @Component로 빈 등록하여 test 가능.</li>
//     *     <ul>
//     *         <li>해당 경우 eg.1. 실제 포스팅을 한 userId가 아닌 이상한 유저의 id가 들어오는 경우(유저인증 파트에서 해결 예정)</li>
//     *         <li>해당 경우 eg.2. 트윗내 투표기능의 기간 설정 시 시작날짜가 종료날짜보다 늦는 경우 등</li>
//     *     </ul>
//     * </ul>
//     * @see clone.twitter.controller.TweetValidator
//     * @see clone.twitter.common.ErrorSerializer
//     */
//    @Test
//    @DisplayName("POST /tweets - 트윗에 받기로한 필드의 종류의 갯수가 일치하고 값이 있으나, 해당 값이 비즈니스 로직상 이상한 경우")
//    void composeTweetBadRequestWrongInput() throws Exception {
//        //TweetComposeRequestDto tweetComposeDto = TweetComposeRequestDto.builder()
//        //    .text("hello, this is my first tweet.")
//        //    .userId("strangeUserId")
//        //    .build();
//        //
//        //this.mockMvc.perform(post("/tweets")
//        //    .contentType(MediaType.APPLICATION_JSON_VALUE)
//        //    .content(this.objectMapper.writeValueAsString(tweetComposeDto)))
//        //    .andDo(print())
//        //    .andExpect(status().isBadRequest())
//        //    .andExpect(jsonPath("$[0].objectName").exists())
//        //    .andExpect(jsonPath("$[0].defaultMessage").exists())
//        //    .andExpect(jsonPath("$[0].code").exists());
//    }
//
////    @Test
////    void getInitialTweets() {
////    }
////
////    @Test
////    void getNextTweets() {
////    }
////
////    @Test
////    void getTweet() {
////    }
////
////    @Test
////    void deleteTweet() {
////    }

}