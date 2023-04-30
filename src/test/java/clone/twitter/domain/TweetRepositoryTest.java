package clone.twitter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.TweetRepositoryV1;
import clone.twitter.repository.UserRepository;
import clone.twitter.repository.UserRepositoryV1;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
class TweetRepositoryTest {

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void afterEach() {

        if (tweetRepository instanceof TweetRepositoryV1) {
            ((TweetRepositoryV1) tweetRepository).clear();
        }

        if (userRepository instanceof UserRepositoryV1) {
            ((UserRepositoryV1) userRepository).clear();
        }
    }

    @Test
    void findAllByOrderByCreatedAtDesc() {

        //given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29",
            "haro",
            LocalDate.of(1999, 9, 9)
        );

        userRepository.save(user);

        Tweet tweet1 = new Tweet("this be testing1", user.getId());
        sleep(1000);
        Tweet tweet2 = new Tweet("this be testing2", user.getId());
        sleep(1000);
        Tweet tweet3 = new Tweet("this be testing3", user.getId());

        //when
        List<Tweet> savedTweets = new ArrayList<>();

        savedTweets.add(tweetRepository.save(tweet3));
        savedTweets.add(tweetRepository.save(tweet2));
        savedTweets.add(tweetRepository.save(tweet1));

        //then
        List<Tweet> foundTweets = tweetRepository.findAllByOrderByCreatedAtDesc();

        assertThat(foundTweets).isEqualTo(savedTweets);
    }

    @Test
    void findAllByUserIdByOrderByCreatedAtDesc() {

        //given
        User user1 = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29",
            "haro",
            LocalDate.of(1999, 9, 9)
        );

        User user2 = new User(
            "hello123",
            "hello@gmail.com",
            "e14c28",
            "hello",
            LocalDate.of(1998, 8, 8)
        );

        User user3 = new User(
            "bonjour123",
            "bonjour@gmail.com",
            "1d052e",
            "bonjour",
            LocalDate.of(1997, 7, 7)
        );

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Tweet tweet1 = new Tweet("hello, this be testing1", user2.getId());
        sleep(1000);
        Tweet tweet2 = new Tweet("bonjour, this be testing1", user3.getId());
        sleep(1000);
        Tweet tweet3 = new Tweet("haro, this be testing1", user1.getId());
        sleep(1000);
        Tweet tweet4 = new Tweet("hello, this be testing2", user2.getId());
        sleep(1000);
        Tweet tweet5 = new Tweet("bonjour, this be testing2", user3.getId());
        sleep(1000);
        Tweet tweet6 = new Tweet("haro, this be testing2", user1.getId());
        sleep(1000);
        Tweet tweet7 = new Tweet("hello, this be testing3", user2.getId());
        sleep(1000);
        Tweet tweet8 = new Tweet("bonjour, this be testing3", user3.getId());
        sleep(1000);
        Tweet tweet9 = new Tweet("haro, this be testing3", user1.getId());

        //when
        List<Tweet> TweetsSavedByUser3 = new ArrayList<>();

        tweetRepository.save(tweet9);
        tweetRepository.save(tweet7);
        tweetRepository.save(tweet6);
        tweetRepository.save(tweet4);
        tweetRepository.save(tweet3);
        tweetRepository.save(tweet1);

        TweetsSavedByUser3.add(tweetRepository.save(tweet8));
        TweetsSavedByUser3.add(tweetRepository.save(tweet5));
        TweetsSavedByUser3.add(tweetRepository.save(tweet2));

        //then
        List<Tweet> foundTweetsOfUser3 = tweetRepository.findAllByUserIdByOrderByCreatedAtDesc(
            user3.getId());

        assertThat(foundTweetsOfUser3).isEqualTo(TweetsSavedByUser3);
    }

    @Test
    void findById() {
        //given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        userRepository.save(user);

        Tweet tweet1 = new Tweet("haro, this be testing1", user.getId());
        Tweet tweet2 = new Tweet("haro, this be testing2", user.getId());
        Tweet tweet3 = new Tweet("haro, this be testing3", user.getId());

        //when
        tweetRepository.save(tweet1);
        tweetRepository.save(tweet3);
        Tweet archivedTweet = tweetRepository.save(tweet2);

        //then
        Optional<Tweet> foundTweet = tweetRepository.findById(archivedTweet.getId());

        assertThat(foundTweet).isEqualTo(Optional.of(archivedTweet));
    }

    @Test
    void save() {
        //given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        userRepository.save(user);

        Tweet tweet = new Tweet("this be testing", user.getId());

        //when
        Tweet savedTweet = tweetRepository.save(tweet);

        //then
        Optional<Tweet> foundTweet = tweetRepository.findById(tweet.getId());

        assertThat(foundTweet).isEqualTo(Optional.of(savedTweet));
    }

    @Test
    void deleteById() {

        //given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        userRepository.save(user);

        Tweet tweet1 = new Tweet("haro, this be testing1", user.getId());
        Tweet tweet2 = new Tweet("haro, this be testing2", user.getId());
        Tweet tweet3 = new Tweet("haro, this be testing3", user.getId());

        //when
        Tweet savedTweet1 = tweetRepository.save(tweet1);
        Tweet savedTweet2 = tweetRepository.save(tweet2);
        Tweet savedTweet3 = tweetRepository.save(tweet3);

        tweetRepository.deleteById(tweet2.getId());

        //then
        Optional<Tweet> foundTweet1 = tweetRepository.findById(savedTweet1.getId());
        Optional<Tweet> foundTweet2 = tweetRepository.findById(savedTweet2.getId());
        Optional<Tweet> foundTweet3 = tweetRepository.findById(savedTweet3.getId());

        assertThat(foundTweet1).isEqualTo(Optional.of(savedTweet1));
        assertThat(foundTweet2).isEmpty();
        assertThat(foundTweet3).isEqualTo(Optional.of(savedTweet3));
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
