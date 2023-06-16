package clone.twitter.domain;

import clone.twitter.repository.LikeTweetRepository;
import clone.twitter.repository.LikeTweetRepositoryV1;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
public class LikeTweetRepositoryTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionStatus status;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    LikeTweetRepository likeTweetRepository;

    @BeforeEach
    void beforeEach() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void afterEach() {
        transactionManager.rollback(status);
    }

    @Test
    void save() {
        // given
        // 유저 객체 생성
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));

        // 유저 객체 정보 DB 저장
        userRepository.save(user1);
        userRepository.save(user2);

        // 트윗 객체 생성
        Tweet tweet1 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet2 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));

        // 유저 객체 정보 DB 저장
        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);

        // 좋아요-트윗 객체 생성: user2 -> tweet1 좋아요, user1 -> tweet2 좋아요
        LikeTweet likeTweet1 = new LikeTweet(tweet1.getId(), user2.getId());
        LikeTweet likeTweet2 = new LikeTweet(tweet2.getId(), user1.getId());

        // when
        // 좋아요-트윗 객체 정보 DB 저장
        likeTweetRepository.save(likeTweet1);
        likeTweetRepository.save(likeTweet2);

        // 좋아요-트윗 DB 정보 조회
        Optional<LikeTweet> foundLikeTweet1 = ((LikeTweetRepositoryV1) likeTweetRepository).findLikeTweetByTweetIdAndUserId(tweet1.getId(), user2.getId());
        Optional<LikeTweet> foundLikeTweet2 = ((LikeTweetRepositoryV1) likeTweetRepository).findLikeTweetByTweetIdAndUserId(tweet2.getId(), user1.getId());

        // then
        // 좋아요-트윗 DB 정보 조회 결과 검증: user2 -> tweet1 좋아요, user1 -> tweet2 좋아요
        Assertions.assertThat(foundLikeTweet1).isEqualTo(Optional.of(likeTweet1));
        Assertions.assertThat(foundLikeTweet2).isEqualTo(Optional.of(likeTweet2));
    }

    @Test
    void deleteByTweetIdAndUserId() {
        // given
        // 유저 객체 생성
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));

        // 유저 객체 정보 DB 저장
        userRepository.save(user1);
        userRepository.save(user2);

        // 트윗 객체 생성
        Tweet tweet1 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet2 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));

        // 유저 객체 정보 DB에 저장
        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);

        // 좋아요-트윗 객체 생성: user2 -> tweet1 좋아요, user1 -> tweet2 좋아요
        LikeTweet likeTweet1 = new LikeTweet(tweet1.getId(), user2.getId());
        LikeTweet likeTweet2 = new LikeTweet(tweet2.getId(), user1.getId());

        // when
        // 좋아요-트윗 객체 정보 DB에 저장
        likeTweetRepository.save(likeTweet1);
        likeTweetRepository.save(likeTweet2);

        // 좋아요-트윗 정보 DB 조회, 결과를 객체에 할당
        Optional<LikeTweet> foundLikeTweet1 = ((LikeTweetRepositoryV1) likeTweetRepository).findLikeTweetByTweetIdAndUserId(tweet1.getId(), user2.getId());
        Optional<LikeTweet> foundLikeTweet2 = ((LikeTweetRepositoryV1) likeTweetRepository).findLikeTweetByTweetIdAndUserId(tweet2.getId(), user1.getId());

        // then
        // 좋아요-트윗 DB 정보 조회 결과 검증: user2 -> tweet1 좋아요, user1 -> tweet2 좋아요
        Assertions.assertThat(foundLikeTweet1).isEqualTo(Optional.of(likeTweet1));
        Assertions.assertThat(foundLikeTweet2).isEqualTo(Optional.of(likeTweet2));

        // 좋아요-트윗 DB 정보 DB 삭제: user2 -> tweet1 좋아요, user1 -> tweet2 좋아요
        likeTweetRepository.deleteByTweetIdAndUserId(tweet1.getId(), user2.getId());
        likeTweetRepository.deleteByTweetIdAndUserId(tweet2.getId(), user1.getId());

        // 좋아요-트윗 DB 정보 DB 삭제 후 DB 조회, 결과를 객체에 할당
        foundLikeTweet1 = ((LikeTweetRepositoryV1) likeTweetRepository).findLikeTweetByTweetIdAndUserId(tweet1.getId(), user2.getId());
        foundLikeTweet2 = ((LikeTweetRepositoryV1) likeTweetRepository).findLikeTweetByTweetIdAndUserId(tweet2.getId(), user1.getId());

        // (좋아요-트윗 DB 정보 삭제 후)DB 조회 결과 검증: user2 -> tweet1 좋아요, user1 -> tweet2 좋아요 두 건 모두 삭제되었으므로 빈 객체가 반환되어야 함
        Assertions.assertThat(foundLikeTweet1).isEmpty();
        Assertions.assertThat(foundLikeTweet2).isEmpty();
    }

    @Test
    void findUsersByTweetIdOrderByCreatedAtDesc() {
        // given
        // 유저 객체 생성
        User user1 = new User("user1", "user1@gmail.com", "password1", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "password2", "user2ProfileName", LocalDate.of(1991, 1, 2));
        User user3 = new User("user3", "user3@gmail.com", "password3", "user3ProfileName", LocalDate.of(1991, 1, 3));
        User user4 = new User("user4", "user4@gmail.com", "password4", "user4ProfileName", LocalDate.of(1991, 1, 4));
        User user5 = new User("user5", "user5@gmail.com", "password5", "user5ProfileName", LocalDate.of(1991, 1, 5));
        User user6 = new User("user6", "user6@gmail.com", "password6", "user6ProfileName", LocalDate.of(1991, 1, 6));
        User user7 = new User("user7", "user7@gmail.com", "password7", "user7ProfileName", LocalDate.of(1991, 1, 7));

        // 유저 객체 정보 DB 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);

        // 트윗 객체 생성
        Tweet tweet1 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet2 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));

        // 트윗 객체 정보 DB에 저장
        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);

        // 좋아요-트윗 객체 생성
        LikeTweet likeTweet1 = new LikeTweet(tweet1.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet2 = new LikeTweet(tweet1.getId(), user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet3 = new LikeTweet(tweet1.getId(), user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 3).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet4 = new LikeTweet(tweet1.getId(), user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 4).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet5 = new LikeTweet(tweet1.getId(), user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 5).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet6 = new LikeTweet(tweet2.getId(), user6.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 6).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet7 = new LikeTweet(tweet2.getId(), user7.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 7).truncatedTo(ChronoUnit.SECONDS));

        // when
        // 좋아요-트윗 객체 정보 DB에 저장
        likeTweetRepository.save(likeTweet1);
        likeTweetRepository.save(likeTweet2);
        likeTweetRepository.save(likeTweet3);
        likeTweetRepository.save(likeTweet4);
        likeTweetRepository.save(likeTweet5);
        likeTweetRepository.save(likeTweet6);
        likeTweetRepository.save(likeTweet7);

        // then
        // 특정 트윗에 좋아요를 누른 유저들의 정보를 pagination limit 갯수 만큼 좋아요 생성 시간 내림차순으로 조회, 결과를 객체에 할당
        List<User> usersByTweetIdOrderByCreatedAtDescForTweet1 = likeTweetRepository.findUsersByTweetIdOrderByCreatedAtDesc(tweet1.getId());
        List<User> usersByTweetIdOrderByCreatedAtDescForTweet2 = likeTweetRepository.findUsersByTweetIdOrderByCreatedAtDesc(tweet2.getId());

        // 특정 트윗에 좋아요를 누른 유저들의 정보 검증1: tweet1에 좋아요를 누른 유저들의 정보는 user5, user4, user3 순서로 조회되어야 함
        Assertions.assertThat(usersByTweetIdOrderByCreatedAtDescForTweet1).containsExactly(user5, user4, user3);
        // 특정 트윗에 좋아요를 누른 유저들의 정보 검증2: tweet2에 좋아요를 누른 유저들의 정보는 user5, user4, user3 순서로 조회되어야 함
        Assertions.assertThat(usersByTweetIdOrderByCreatedAtDescForTweet2).containsExactly(user7, user6);
    }

    @Test
    void findUsersByTweetIdAndUserIdOrderByCreatedAtDesc() {
        // given
        // 유저 객체 생성
        User user1 = new User("user1", "user1@gmail.com", "password1", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "password2", "user2ProfileName", LocalDate.of(1991, 1, 2));
        User user3 = new User("user3", "user3@gmail.com", "password3", "user3ProfileName", LocalDate.of(1991, 1, 3));
        User user4 = new User("user4", "user4@gmail.com", "password4", "user4ProfileName", LocalDate.of(1991, 1, 4));
        User user5 = new User("user5", "user5@gmail.com", "password5", "user5ProfileName", LocalDate.of(1991, 1, 5));
        User user6 = new User("user6", "user6@gmail.com", "password6", "user6ProfileName", LocalDate.of(1991, 1, 6));
        User user7 = new User("user7", "user7@gmail.com", "password7", "user7ProfileName", LocalDate.of(1991, 1, 7));

        // 유저 객체 정보 DB 저장
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);

        // 트윗 객체 생성
        Tweet tweet1 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));

        // 트윗 객체 정보 DB 저장
        tweetRepository.save(tweet1);

        // 좋아요-트윗 객체 생성
        LikeTweet likeTweet1 = new LikeTweet(tweet1.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet2 = new LikeTweet(tweet1.getId(), user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet3 = new LikeTweet(tweet1.getId(), user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 3).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet4 = new LikeTweet(tweet1.getId(), user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 4).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet5 = new LikeTweet(tweet1.getId(), user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 5).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet6 = new LikeTweet(tweet1.getId(), user6.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 6).truncatedTo(ChronoUnit.SECONDS));
        LikeTweet likeTweet7 = new LikeTweet(tweet1.getId(), user7.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 7).truncatedTo(ChronoUnit.SECONDS));

        // when
        // 좋아요-트윗 객체 정보 DB 저장
        likeTweetRepository.save(likeTweet1);
        likeTweetRepository.save(likeTweet2);
        likeTweetRepository.save(likeTweet3);
        likeTweetRepository.save(likeTweet4);
        likeTweetRepository.save(likeTweet5);
        likeTweetRepository.save(likeTweet6);
        likeTweetRepository.save(likeTweet7);

        // then
        // 특정 트윗에 좋아요를 누른 유저들의 최초 정보 목록 조회 후(최신순으로 user7, user6, user5), 스크롤 다운 시 맨 마지막에 있던 user5의 좋아요 생성시간을 기준으로 생성시간 내림차순으로 유저 추가 조회, 유저 정보목록을 반환 받아 결과정보 객체에 할당
        List<User> moreUsersByTweetIdOrderByCreatedAtDesc = likeTweetRepository.findUsersByTweetIdAndUserIdOrderByCreatedAtDesc(tweet1.getId(), user5.getId());

        // 특정 트윗에 좋아요를 누른 유저들을 추가 조회한 후 유저 정보 검증: tweet1에 좋아요를 누른 다음 유저들의 정보는 user4, user3, user2 순서로 조회되어야 함
        Assertions.assertThat(moreUsersByTweetIdOrderByCreatedAtDesc).containsExactly(user4, user3, user2);
    }
}
