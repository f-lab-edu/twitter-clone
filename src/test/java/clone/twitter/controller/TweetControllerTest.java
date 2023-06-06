package clone.twitter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.request.TweetPostRequestDto;
import clone.twitter.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TweetControllerTest {
    // springboot를 사용중일 시 mapping jackson json이 의존성으로 설정돼있으면 ObjectMapper가 자동으로 bean으로 등록
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionStatus status;

    @BeforeEach
    void beforeEach() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        // tweet table의 user_id 필드의 foreign key constraint으로 인해 더미 user 객체도 각 테스트 전에 생성 필요
        User user = new User("idOfHarry", "harry", "harry@gmail.com", "b03b29", "hello", LocalDate.of(1999, 9, 9), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        userRepository.save(user);
    }

    @AfterEach
    void afterEach() {
        transactionManager.rollback(status);
    }

    @DisplayName("POST /tweets - 정상적인 케이스")
    @Test
    void postTweetCorrectInputWithDto() throws Exception {
        TweetPostRequestDto tweetPostDto = TweetPostRequestDto.builder()
            .text("hello, this is my first tweet.")
            .userId("idOfHarry")
            .build();

        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweetPostDto)))
            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
            .andExpect(status().isCreated()) // 201이라고 직접 입력하는 것보다 type-safe
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
    }

    // 아래 postTweetBadRequest() 테스트와 병행 불가. application.yml에서 spring.jackson.deserialization.fail-on-unknown-properties 설정 조정 필요.
    @DisplayName("POST /tweets - 입력값 제한하기. 불명의 필드(properties) 데이터가 같이 들어올 경우 받기로 한 값 외 무시하고 정상처리")
    @Test
    void postTweetExcessiveInput() throws Exception {
        Tweet tweet = Tweet.builder()
            .id("1")
            .text("hello, this is my first tweet.")
            .userId("idOfHarry")
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweet)))
            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
            .andExpect(status().isCreated()) // 201이라고 직접 입력하는 것보다 type-safe
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            // 하기 코드: tweet의 id, createdAt 필드들은 상관없는(TweetRequestDto의 필드에 없는) 더미 데이터가 요청으로 들어와도 무시되어야 한다
            .andExpect(jsonPath("id").value(Matchers.not("1")))
            .andExpect(jsonPath("createdAt").value(Matchers.not(LocalDateTime.of(2023, 6, 1, 1, 1, 1))));
    }

    @DisplayName("POST /tweets - 입력값 이외 에러 발생. 불명의 더미 필드(properties) 데이터가 같이 들어올 경우 bad request로 응답")
    @Test
    void postTweetBadRequest() throws Exception {
        Tweet tweet = Tweet.builder()
            .id("1")
            .text("hello, this is my first tweet.")
            .userId("idOfHarry")
            .createdAt(LocalDateTime.of(2023, 6, 1, 1, 1, 1))
            .build();

        // 원래대로면 결과는 이전 테스트(postTweetExcessiveInput)와 동일(201 반환) -> springboot가 제공하는 properties를 활용한 object mapper 확장기능 사용(401 반환)
        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweet)))
            .andDo(print()) // 어떤 요청과 응답이 오갔는지 테스트 로그에서 확인 가능
            .andExpect(status().isBadRequest()); // 400이라고 직접 입력하는 것보다 type-safe
    }

    @DisplayName("POST /tweets - Bad Request 처리하기1. 필드의 종류와 갯수가 맞으나 값이 비어있는 경우")
    @Test
    void postTweetBadRequestEmptyInput() throws Exception {
        TweetPostRequestDto tweetPostDto = TweetPostRequestDto.builder().build();

        this.mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(tweetPostDto)))
            .andExpect(status().isBadRequest());
    }

    //// eg.1. userId가 검증이 안되고 이상한 유저의 id가 들어오는 경우(유저인증 파트에서 해결 예정). eg.2. 투표 기간 설정 시 시작날짜가 종료날짜보다 늦는 경우 등. 현재는 해당사항 없음. 별도 validator 클래스 규정(Errors.rejectValue()...)하고 @Component로 빈 등록 등 필요.
    //@DisplayName("POST /tweets - Bad Request 처리하기2. 필드의 종류의 갯수가 맞으나 값이 비즈니스 로직상 이상한 경우")
    //@Test
    //void postTweetBadRequestWrongInput() throws Exception {
    //
    //}

//    @Test
//    void getInitialTweets() {
//    }
//
//    @Test
//    void getNextTweets() {
//    }
//
//    @Test
//    void getTweet() {
//    }
//
//    @Test
//    void deleteTweet() {
//    }
}