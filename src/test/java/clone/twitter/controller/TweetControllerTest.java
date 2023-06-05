package clone.twitter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class TweetControllerTest {

    @Autowired
    MockMvc mockMvc;

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
    void postTweet() throws Exception {
        mockMvc.perform(post("/tweets")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    void deleteTweet() {
    }
}