package clone.twitter.service;

import clone.twitter.dto.Tweet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class TweetServiceTest {
    @Autowired
    private TweetService tweetService;

    @Test
    @Rollback
    @DisplayName("작성한 트윗이 등록 성공")
    public void testPostTweetSuccess() {
        //given, when
        Tweet newTweet = new Tweet("Testing", "sjlee");
        tweetService.createNewTweet(newTweet);

        //then
        Tweet getTweetFromDB = tweetService.findTweetByTweetId(newTweet.getId());
        assertThat(newTweet.getId()).isEqualTo(getTweetFromDB.getId());
    }

    @Test
    @Rollback
    @DisplayName("해당 ID를 가진 트윗이 삭제됨")
    public void testDeleteTweetSuccess() {
        //given
        Tweet newTweet = new Tweet("Testing", "sjlee");
        tweetService.createNewTweet(newTweet);

        //when
        tweetService.deleteTweet(newTweet.getId());

        //then
        Tweet SearchTweetFromDB = tweetService.findTweetByTweetId(newTweet.getId());
        assertThat(SearchTweetFromDB).isEqualTo(null);
    }

    @Test
    @Rollback
    @DisplayName("해당 Tweet ID를 가진 트윗 정보를 가져옴")
    public void testFindTweetByTweetId() {
        //given
        Tweet newTweet = new Tweet("Testing", "sjlee");
        tweetService.createNewTweet(newTweet);

        //when
        tweetService.findTweetByTweetId(newTweet.getId());

        //then
        Tweet SearchTweetFromDB = tweetService.findTweetByTweetId(newTweet.getId());

        assertThat(SearchTweetFromDB.getId()).isEqualTo(newTweet.getId());
        assertThat(SearchTweetFromDB.getText()).isEqualTo(newTweet.getText());
        assertThat(SearchTweetFromDB.getUserId()).isEqualTo(newTweet.getUserId());
        assertThat(SearchTweetFromDB.getCreatedAt()).isEqualTo(newTweet.getCreatedAt());
    }

    @Test
    @Rollback
    @DisplayName("해당 User ID를 가진 트윗 리스트를 가져옴")
    public void testFindTweetsByUserId() {
        //given
        Tweet newTweet = new Tweet("Testing", "sjlee");
        Tweet newTweet2 = new Tweet("Testing2", "sjlee");
        Tweet newTweet3 = new Tweet("Testing3", "sjlee");
        Tweet newTweet4 = new Tweet("Testing4", "sjlee");

        tweetService.createNewTweet(newTweet);
        tweetService.createNewTweet(newTweet2);
        tweetService.createNewTweet(newTweet3);
        tweetService.createNewTweet(newTweet4);

        //when
        List<Tweet> findTweetList = tweetService.findTweetsByUserId("sjlee");

        //then
        assertThat(findTweetList.stream().map(Tweet::getId)).contains(newTweet.getId());
        assertThat(findTweetList.stream().map(Tweet::getId)).contains(newTweet2.getId());
        assertThat(findTweetList.stream().map(Tweet::getId)).contains(newTweet3.getId());
        assertThat(findTweetList.stream().map(Tweet::getId)).contains(newTweet4.getId());
    }
}
