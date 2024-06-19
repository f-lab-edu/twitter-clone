//package clone.twitter.domain;
//
//import clone.twitter.repository.FollowRepository;
//import clone.twitter.repository.dto.UserFollowDto;
//import clone.twitter.repository.UserRepository;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Optional;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.TransactionStatus;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.DefaultTransactionDefinition;
//
//@Slf4j
//@Transactional
//@SpringBootTest
//class FollowRepositoryTest {
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    FollowRepository followRepository;
//
//    @Autowired
//    PlatformTransactionManager transactionManager;
//
//    TransactionStatus status;
//
//    @BeforeEach
//    void beforeEach() {
//        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//    }
//
//    @AfterEach
//    void afterEach() {
//        transactionManager.rollback(status);
//    }
//
//    @Test
//    void follow() {
//        // given
//        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
//        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
//
//        Follow follow1 = new Follow(user1.getId(), user2.getId());
//        Follow follow2 = new Follow(user2.getId(), user1.getId());
//
//        // when
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//        followRepository.save(follow1);
//        followRepository.save(follow2);
//
//        // then
//        Optional<Follow> foundFollow1 = followRepository.findByFollowerIdAndFolloweeId(user1.getId(), user2.getId());
//        Optional<Follow> foundFollow2 = followRepository.findByFollowerIdAndFolloweeId(user2.getId(), user1.getId());
//
//        Assertions.assertThat(foundFollow1).isNotNull().isEqualTo(Optional.of(follow1));
//        Assertions.assertThat(foundFollow2).isNotNull().isEqualTo(Optional.of(follow2));
//    }
//
//    @Test
//    void unfollow() {
//        // given
//        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
//        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
//
//        Follow follow1 = new Follow(user1.getId(), user2.getId());
//        Follow follow2 = new Follow(user2.getId(), user1.getId());
//
//        // when
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//        followRepository.save(follow1);
//        followRepository.save(follow2);
//
//        followRepository.delete(follow1);
//
//        // then
//        Optional<Follow> foundFollow1 = followRepository.findByFollowerIdAndFolloweeId(user1.getId(), user2.getId());
//        Optional<Follow> foundFollow2 = followRepository.findByFollowerIdAndFolloweeId(user2.getId(), user1.getId());
//
//        Assertions.assertThat(foundFollow1).isEmpty();
//
//        Assertions.assertThat(foundFollow2).isNotEmpty().isEqualTo(Optional.of(follow2));
//    }
//
//    @Test
//    void findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc() {
//        // given
//        // User 객체 생성
//        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
//        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
//        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
//        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
//        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));
//        User user6 = new User("user6", "user6@gmail.com", "FFFFFF", "user6ProfileName", LocalDate.of(1996, 6, 6));
//        User user7 = new User("user7", "user7@gmail.com", "GGGGGG", "user7ProfileName", LocalDate.of(1997, 7, 7));
//        User user8 = new User("user8", "user8@gmail.com", "HHHHHH", "user8ProfileName", LocalDate.of(1998, 8, 8));
//
//        // user1이 순서대로 다른 모든 유저를 팔로우
//        Follow follow1 = new Follow(user1.getId(), user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow2 = new Follow(user1.getId(), user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow3 = new Follow(user1.getId(), user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 3).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow4 = new Follow(user1.getId(), user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 4).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow5 = new Follow(user1.getId(), user6.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 5).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow6 = new Follow(user1.getId(), user7.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 6).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow7 = new Follow(user1.getId(), user8.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 7).truncatedTo(ChronoUnit.SECONDS));
//        // 모든 유저가 순서대로 user1을 팔로우
//        Follow follow8 = new Follow(user2.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 8).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow9 = new Follow(user3.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 9).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow10 = new Follow(user4.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 10).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow11 = new Follow(user5.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 11).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow12 = new Follow(user6.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 12).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow13 = new Follow(user7.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 13).truncatedTo(ChronoUnit.SECONDS));
//        Follow follow14 = new Follow(user8.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 14).truncatedTo(ChronoUnit.SECONDS));
//
//        // when
//        // 모든 user 객체 DB 저장
//        userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
//        userRepository.save(user4);
//        userRepository.save(user5);
//        userRepository.save(user6);
//        userRepository.save(user7);
//        userRepository.save(user8);
//
//        // 모든 follow 객체 DB 저장
//        followRepository.save(follow1);
//        followRepository.save(follow2);
//        followRepository.save(follow3);
//        followRepository.save(follow4);
//        followRepository.save(follow5);
//        followRepository.save(follow6);
//        followRepository.save(follow7);
//        followRepository.save(follow8);
//        followRepository.save(follow9);
//        followRepository.save(follow10);
//        followRepository.save(follow11);
//        followRepository.save(follow12);
//        followRepository.save(follow13);
//        followRepository.save(follow14);
//
//        // then
//        // CASE1-1: user1의 Following 리스트 조회 및 검증(팔로우 시간 내림차순). 첫 번째 페이지.
//        List<UserFollowDto> foundFollowingPage1 = followRepository.findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(user1.getId(), null, null);
//
//        Assertions.assertThat(foundFollowingPage1).containsExactly(new UserFollowDto(user8, follow7), new UserFollowDto(user7, follow6), new UserFollowDto(user6, follow5));
//
//        // CASE1-2: user1의 Following 리스트 추가 조회 및 검증(팔로우 시간 내림차순). 두 번째 페이지. 이전 CASE1-1 목록 마지막 유저의 follow 객체의 createdAt을 기준으로 조회.
//        List<UserFollowDto> foundFollowingPage2 = followRepository.findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(user1.getId(), null, follow5.getCreatedAt());
//
//        Assertions.assertThat(foundFollowingPage2).containsExactly(new UserFollowDto(user5, follow4), new UserFollowDto(user4, follow3), new UserFollowDto(user3, follow2));
//
//        // CASE2-1: user1의 Followed 리스트 조회 및 검증(팔로우 시간 내림차순). 첫 번째 페이지.
//        List<UserFollowDto> foundFollowedPage1 = followRepository.findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(null, user1.getId(), null);
//
//        Assertions.assertThat(foundFollowedPage1).containsExactly(new UserFollowDto(user8, follow14), new UserFollowDto(user7, follow13), new UserFollowDto(user6, follow12));
//
//        // CASE2-2: user1의 Followed 리스트 추가 조회 및 검증(팔로우 시간 내림차순). 두 번째 페이지. 이전 CASE2-1 목록 마지막 유저의 follow 객체의 createdAt을 기준으로 조회.
//        List<UserFollowDto> foundFollowedPage2 = followRepository.findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(null, user1.getId(), follow12.getCreatedAt());
//
//        Assertions.assertThat(foundFollowedPage2).containsExactly(new UserFollowDto(user5, follow11), new UserFollowDto(user4, follow10), new UserFollowDto(user3, follow9));
//    }
//}
