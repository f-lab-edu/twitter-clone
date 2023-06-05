package clone.twitter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import clone.twitter.domain.Tweet;
import clone.twitter.service.TweetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

//@SpringBootTest
@WebMvcTest
class TweetControllerTest {
    // springboot를 사용중일 시 mapping jackson json이 의존성으로 설정돼있으면 ObjectMapper가 자동으로 bean으로 등록
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TweetService tweetService;

    @Test
    void postTweet() throws Exception {
        Tweet tweet = Tweet.builder()
            .text("hello, this is my first tweet.")
            .userId("harry")
            .build();

        tweet.setId(UUID.randomUUID().toString());

        tweet.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Mockito.when(tweetService.postTweet(tweet)).thenReturn(tweet);

        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(tweet)))
            .andDo(print()) // 어떤 요청과 응답이 오갔는지 확인 가능
            .andExpect(status().isCreated()) // 201과 같음(201이라고 직접 입력하는 것보다 type-safe)
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
    }

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

//    @Test
//    void deleteTweet() {
//    }
}