package clone.twitter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.repository.UserRepository;
import clone.twitter.service.TweetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
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

    @Test
    void postTweet() throws Exception {

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

    @Test
    void getInitialTweets() {
    }

    @Test
    void getNextTweets() {
    }

    @Test
    void getTweet() {
    }

    @Test
    void deleteTweet() {
    }
}