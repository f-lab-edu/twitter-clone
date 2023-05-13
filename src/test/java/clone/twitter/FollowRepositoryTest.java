package clone.twitter.domain;

import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
class FollowRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

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
    void follow() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));

        Follow follow1 = new Follow(user1.getId(), user2.getId());
        Follow follow2 = new Follow(user2.getId(), user1.getId());

        // when
        userRepository.save(user1);
        userRepository.save(user2);

        followRepository.follow(follow1);
        followRepository.follow(follow2);

        // then
        Optional<Follow> foundFollow1 = followRepository.findByIds(user1.getId(), user2.getId());
        Optional<Follow> foundFollow2 = followRepository.findByIds(user2.getId(), user1.getId());

        Assertions.assertThat(foundFollow1).isNotNull().isEqualTo(Optional.of(follow1));
        Assertions.assertThat(foundFollow2).isNotNull().isEqualTo(Optional.of(follow2));
    }

    @Test
    void unfollow() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));

        Follow follow1 = new Follow(user1.getId(), user2.getId());
        Follow follow2 = new Follow(user2.getId(), user1.getId());

        // when
        userRepository.save(user1);
        userRepository.save(user2);

        followRepository.follow(follow1);
        followRepository.follow(follow2);

        followRepository.unfollow(follow1);

        // then
        Optional<Follow> foundFollow1 = followRepository.findByIds(user1.getId(), user2.getId());
        Optional<Follow> foundFollow2 = followRepository.findByIds(user2.getId(), user1.getId());

        Assertions.assertThat(foundFollow1).isEmpty();

        Assertions.assertThat(foundFollow2).isNotEmpty().isEqualTo(Optional.of(follow2));
    }

    @Test
    void findByFollowerIdOrderByCreatedAtDesc() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));
        User user6 = new User("user6", "user6@gmail.com", "FFFFFF", "user6ProfileName", LocalDate.of(1996, 6, 6));

        Follow follow1 = new Follow(user1.getId(), user2.getId());

        sleep(1000);

        Follow follow2 = new Follow(user1.getId(), user3.getId());

        sleep(1000);

        Follow follow3 = new Follow(user1.getId(), user4.getId());

        sleep(1000);

        Follow follow4 = new Follow(user1.getId(), user5.getId());

        sleep(1000);

        Follow follow5 = new Follow(user1.getId(), user6.getId());

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);

        followRepository.follow(follow1);
        followRepository.follow(follow2);
        followRepository.follow(follow3);
        followRepository.follow(follow4);
        followRepository.follow(follow5);

        // then
        List<User> foundUsersOrderByCreatedAtDesc = followRepository.findByFollowerIdOrderByCreatedAtDesc(user1.getId());

        log.info(String.valueOf(foundUsersOrderByCreatedAtDesc.size()));
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.size()).isEqualTo(3);

        for (User user : foundUsersOrderByCreatedAtDesc) {
            log.info(String.valueOf(user));
        }
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(0)).isEqualTo(user6);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(1)).isEqualTo(user5);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(2)).isEqualTo(user4);
    }

    @Test
    void findNextByFollowerIdOrderByCreatedAtDesc() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));
        User user6 = new User("user6", "user6@gmail.com", "FFFFFF", "user6ProfileName", LocalDate.of(1996, 6, 6));
        User user7 = new User("user7", "user7@gmail.com", "GGGGGG", "user7ProfileName", LocalDate.of(1997, 7, 7));
        User user8 = new User("user8", "user8@gmail.com", "HHHHHH", "user8ProfileName", LocalDate.of(1998, 8, 8));

        Follow follow1 = new Follow(user1.getId(), user2.getId());

        sleep(1000);

        Follow follow2 = new Follow(user1.getId(), user3.getId());

        sleep(1000);

        Follow follow3 = new Follow(user1.getId(), user4.getId());

        sleep(1000);

        Follow follow4 = new Follow(user1.getId(), user5.getId());

        sleep(1000);

        Follow follow5 = new Follow(user1.getId(), user6.getId());

        sleep(1000);

        Follow follow6 = new Follow(user1.getId(), user7.getId());

        sleep(1000);

        Follow follow7 = new Follow(user1.getId(), user8.getId());

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);

        followRepository.follow(follow1);
        followRepository.follow(follow2);
        followRepository.follow(follow3);
        followRepository.follow(follow4);
        followRepository.follow(follow5);
        followRepository.follow(follow6);
        followRepository.follow(follow7);

        // then
        List<User> foundUsersOrderByCreatedAtDesc = followRepository.findNextByFollowerIdOrderByCreatedAtDesc(user1.getId(), user6.getId());

        log.info(String.valueOf(foundUsersOrderByCreatedAtDesc.size()));

        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.size()).isEqualTo(3);

        for (User user : foundUsersOrderByCreatedAtDesc) {
            log.info(String.valueOf(user));
        }

        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(0)).isEqualTo(user5);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(1)).isEqualTo(user4);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(2)).isEqualTo(user3);
    }

    @Test
    void findByFolloweeIdOrderByCreatedAtDesc() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));
        User user6 = new User("user6", "user6@gmail.com", "FFFFFF", "user6ProfileName", LocalDate.of(1996, 6, 6));

        Follow follow1 = new Follow(user2.getId(), user1.getId());

        sleep(1000);

        Follow follow2 = new Follow(user3.getId(), user1.getId());

        sleep(1000);

        Follow follow3 = new Follow(user4.getId(), user1.getId());

        sleep(1000);

        Follow follow4 = new Follow(user5.getId(), user1.getId());

        sleep(1000);

        Follow follow5 = new Follow(user6.getId(), user1.getId());

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);

        followRepository.follow(follow1);
        followRepository.follow(follow2);
        followRepository.follow(follow3);
        followRepository.follow(follow4);
        followRepository.follow(follow5);

        // then
        List<User> foundUsersOrderByCreatedAtDesc = followRepository.findByFolloweeIdOrderByCreatedAtDesc(user1.getId());

        log.info(String.valueOf(foundUsersOrderByCreatedAtDesc.size()));

        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.size()).isEqualTo(3);

        for (User user : foundUsersOrderByCreatedAtDesc) {
            log.info(String.valueOf(user));
        }
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(0)).isEqualTo(user6);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(1)).isEqualTo(user5);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(2)).isEqualTo(user4);
    }

    @Test
    void findNextByFolloweeIdOrderByCreatedAtDesc() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));
        User user6 = new User("user6", "user6@gmail.com", "FFFFFF", "user6ProfileName", LocalDate.of(1996, 6, 6));
        User user7 = new User("user7", "user7@gmail.com", "GGGGGG", "user7ProfileName", LocalDate.of(1997, 7, 7));
        User user8 = new User("user8", "user8@gmail.com", "HHHHHH", "user8ProfileName", LocalDate.of(1998, 8, 8));

        Follow follow1 = new Follow(user2.getId(), user1.getId());

        sleep(1000);

        Follow follow2 = new Follow(user3.getId(), user1.getId());

        sleep(1000);

        Follow follow3 = new Follow(user4.getId(), user1.getId());

        sleep(1000);

        Follow follow4 = new Follow(user5.getId(), user1.getId());

        sleep(1000);

        Follow follow5 = new Follow(user6.getId(), user1.getId());

        sleep(1000);

        Follow follow6 = new Follow(user7.getId(), user1.getId());

        sleep(1000);

        Follow follow7 = new Follow(user8.getId(), user1.getId());

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);

        followRepository.follow(follow1);
        followRepository.follow(follow2);
        followRepository.follow(follow3);
        followRepository.follow(follow4);
        followRepository.follow(follow5);
        followRepository.follow(follow6);
        followRepository.follow(follow7);

        // then
        List<User> foundUsersOrderByCreatedAtDesc = followRepository.findNextByFolloweeIdOrderByCreatedAtDesc(user1.getId(), user6.getId());

        log.info(String.valueOf(foundUsersOrderByCreatedAtDesc.size()));

        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.size()).isEqualTo(3);

        for (User user : foundUsersOrderByCreatedAtDesc) {
            log.info(String.valueOf(user));
        }

        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(0)).isEqualTo(user5);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(1)).isEqualTo(user4);
        Assertions.assertThat(foundUsersOrderByCreatedAtDesc.get(2)).isEqualTo(user3);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
