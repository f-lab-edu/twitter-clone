package clone.twitter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@Transactional
@SpringBootTest
class TweetRepositoryTest {
    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionStatus status;

    @BeforeEach
    void beforeEach() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void afterEach() {
        transactionManager.rollback(status);
    }

    @Test
    void findInitialTimelinePageTweets() {
        // 테스트 선행조건: user repository + follow repository 구현
    }

    @Test
    void findNextTimelinePageTweets() {
        // 테스트 선행조건: user repository + follow repository 구현
    }

    @Test
    void findById() {
        //given
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));

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
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));

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
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));

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
