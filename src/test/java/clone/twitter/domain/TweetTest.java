//package clone.twitter.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.Test;
//
//class TweetTest {
//    @Test
//    public void builder() {
//        Tweet tweet = Tweet.builder().build();
//        assertThat(tweet).isNotNull();
//    }
//
//    @Test
//    public void javaBean() {
//        // given
//        String tweetId = "TWEET_ID";
//        String text = "this is tweet test.";
//
//        // when
//        Tweet tweet = new Tweet();
//        tweet.setId(tweetId);
//        tweet.setText(text);
//
//        // then
//        assertThat(tweet.getId()).isEqualTo(tweetId);
//        assertThat(tweet.getText()).isEqualTo(text);
//    }
//}