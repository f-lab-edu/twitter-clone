package clone.twitter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import clone.twitter.repository.dto.UserFollowDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
    FollowRepository followRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    TransactionStatus status;

    @BeforeEach
    void beforeEach() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
    }

    @AfterEach
    void afterEach() {
        transactionManager.rollback(status);
    }

    @Test
    void findInitialTimelinePageTweets() {
        // given
        // 유저 생성
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));

        // 팔로우 생성 user1 -> user2, user3
        Follow follow1 = new Follow(user1.getId(), user2.getId());
        Follow follow2 = new Follow(user1.getId(), user3.getId());

        // 팔로우 생성 user2 -> user3, user4
        Follow follow3 = new Follow(user2.getId(), user3.getId());
        Follow follow4 = new Follow(user2.getId(), user4.getId());

        // 팔로우 생성 user3 -> user4, user5
        Follow follow5 = new Follow(user3.getId(), user4.getId());
        Follow follow6 = new Follow(user3.getId(), user5.getId());

        // 팔로우 생성 user4 -> user5, user1
        Follow follow7 = new Follow(user4.getId(), user5.getId());
        Follow follow8 = new Follow(user4.getId(), user1.getId());

        // 팔로우 생성 user5 -> user1, user2
        Follow follow9 = new Follow(user5.getId(), user1.getId());
        Follow follow10 = new Follow(user5.getId(), user2.getId());

        // 유저별 트윗1 생성
        Tweet tweet1 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet2 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet3 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 3).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet4 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 4).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet5 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 5).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗2 생성
        Tweet tweet6 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 6).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet7 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 7).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet8 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 8).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet9 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 9).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet10 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 10).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗3 생성
        Tweet tweet11 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 11).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet12 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 12).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet13 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 13).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet14 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 14).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet15 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 15).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗4 생성
        Tweet tweet16 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 16).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet17 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 17).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet18 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 18).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet19 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 19).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet20 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 20).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗5 생성
        Tweet tweet21 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 21).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet22 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 22).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet23 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 23).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet24 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 24).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet25 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 25).truncatedTo(ChronoUnit.SECONDS));

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);

        followRepository.save(follow1);
        followRepository.save(follow2);
        followRepository.save(follow3);
        followRepository.save(follow4);
        followRepository.save(follow5);
        followRepository.save(follow6);
        followRepository.save(follow7);
        followRepository.save(follow8);
        followRepository.save(follow9);
        followRepository.save(follow10);

        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);
        tweetRepository.save(tweet3);
        tweetRepository.save(tweet4);
        tweetRepository.save(tweet5);
        tweetRepository.save(tweet6);
        tweetRepository.save(tweet7);
        tweetRepository.save(tweet8);
        tweetRepository.save(tweet9);
        tweetRepository.save(tweet10);
        tweetRepository.save(tweet11);
        tweetRepository.save(tweet12);
        tweetRepository.save(tweet13);
        tweetRepository.save(tweet14);
        tweetRepository.save(tweet15);
        tweetRepository.save(tweet16);
        tweetRepository.save(tweet17);
        tweetRepository.save(tweet18);
        tweetRepository.save(tweet19);
        tweetRepository.save(tweet20);
        tweetRepository.save(tweet21);
        tweetRepository.save(tweet22);
        tweetRepository.save(tweet23);
        tweetRepository.save(tweet24);
        tweetRepository.save(tweet25);

        // then
        List<Tweet> initialTimelinePageTweetsForUser1 = tweetRepository.findInitialTimelinePageTweets(user1.getId());
        List<Tweet> initialTimelinePageTweetsForUser2 = tweetRepository.findInitialTimelinePageTweets(user2.getId());
        List<Tweet> initialTimelinePageTweetsForUser3 = tweetRepository.findInitialTimelinePageTweets(user3.getId());
        List<Tweet> initialTimelinePageTweetsForUser4 = tweetRepository.findInitialTimelinePageTweets(user4.getId());
        List<Tweet> initialTimelinePageTweetsForUser5 = tweetRepository.findInitialTimelinePageTweets(user5.getId());

        assertThat(initialTimelinePageTweetsForUser1).containsExactly(tweet23, tweet22, tweet21, tweet18, tweet17);
        assertThat(initialTimelinePageTweetsForUser2).containsExactly(tweet24, tweet23, tweet22, tweet19, tweet18);
        assertThat(initialTimelinePageTweetsForUser3).containsExactly(tweet25, tweet24, tweet23, tweet20, tweet19);
        assertThat(initialTimelinePageTweetsForUser4).containsExactly(tweet25, tweet24, tweet21, tweet20, tweet19);
        assertThat(initialTimelinePageTweetsForUser5).containsExactly(tweet25, tweet22, tweet21, tweet20, tweet17);
    }

    @Test
    void findNextTimelinePageTweets() {
        // given
        // 유저 생성
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));

        // 팔로우 생성 user1 -> user2, user3
        Follow follow1 = new Follow(user1.getId(), user2.getId());
        Follow follow2 = new Follow(user1.getId(), user3.getId());

        // 팔로우 생성 user2 -> user3, user4
        Follow follow3 = new Follow(user2.getId(), user3.getId());
        Follow follow4 = new Follow(user2.getId(), user4.getId());

        // 팔로우 생성 user3 -> user4, user5
        Follow follow5 = new Follow(user3.getId(), user4.getId());
        Follow follow6 = new Follow(user3.getId(), user5.getId());

        // 팔로우 생성 user4 -> user5, user1
        Follow follow7 = new Follow(user4.getId(), user5.getId());
        Follow follow8 = new Follow(user4.getId(), user1.getId());

        // 팔로우 생성 user5 -> user1, user2
        Follow follow9 = new Follow(user5.getId(), user1.getId());
        Follow follow10 = new Follow(user5.getId(), user2.getId());

        // 유저별 트윗1 생성
        Tweet tweet1 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet2 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet3 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 3).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet4 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 4).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet5 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet1 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 5).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗2 생성
        Tweet tweet6 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 6).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet7 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 7).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet8 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 8).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet9 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 9).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet10 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet2 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 10).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗3 생성
        Tweet tweet11 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 11).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet12 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 12).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet13 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 13).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet14 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 14).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet15 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet3 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 15).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗4 생성
        Tweet tweet16 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 16).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet17 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 17).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet18 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 18).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet19 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 19).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet20 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet4 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 20).truncatedTo(ChronoUnit.SECONDS));

        // 유저별 트윗5 생성
        Tweet tweet21 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user1", user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 21).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet22 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user2", user2.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 22).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet23 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user3", user3.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 23).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet24 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user4", user4.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 24).truncatedTo(ChronoUnit.SECONDS));
        Tweet tweet25 = new Tweet(UUID.randomUUID().toString(), "haro, this be tweet5 of user5", user5.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 25).truncatedTo(ChronoUnit.SECONDS));

        // when
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);

        followRepository.save(follow1);
        followRepository.save(follow2);
        followRepository.save(follow3);
        followRepository.save(follow4);
        followRepository.save(follow5);
        followRepository.save(follow6);
        followRepository.save(follow7);
        followRepository.save(follow8);
        followRepository.save(follow9);
        followRepository.save(follow10);

        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);
        tweetRepository.save(tweet3);
        tweetRepository.save(tweet4);
        tweetRepository.save(tweet5);
        tweetRepository.save(tweet6);
        tweetRepository.save(tweet7);
        tweetRepository.save(tweet8);
        tweetRepository.save(tweet9);
        tweetRepository.save(tweet10);
        tweetRepository.save(tweet11);
        tweetRepository.save(tweet12);
        tweetRepository.save(tweet13);
        tweetRepository.save(tweet14);
        tweetRepository.save(tweet15);
        tweetRepository.save(tweet16);
        tweetRepository.save(tweet17);
        tweetRepository.save(tweet18);
        tweetRepository.save(tweet19);
        tweetRepository.save(tweet20);
        tweetRepository.save(tweet21);
        tweetRepository.save(tweet22);
        tweetRepository.save(tweet23);
        tweetRepository.save(tweet24);
        tweetRepository.save(tweet25);

        // then
        List<Tweet> nextTimelinePageTweetsForUser1 = tweetRepository.findNextTimelinePageTweets(user1.getId(), tweet17.getCreatedAt());
        List<Tweet> nextTimelinePageTweetsForUser2 = tweetRepository.findNextTimelinePageTweets(user2.getId(), tweet18.getCreatedAt());
        List<Tweet> nextTimelinePageTweetsForUser3 = tweetRepository.findNextTimelinePageTweets(user3.getId(), tweet19.getCreatedAt());
        List<Tweet> nextTimelinePageTweetsForUser4 = tweetRepository.findNextTimelinePageTweets(user4.getId(), tweet20.getCreatedAt());
        List<Tweet> nextTimelinePageTweetsForUser5 = tweetRepository.findNextTimelinePageTweets(user5.getId(), tweet17.getCreatedAt());

        assertThat(nextTimelinePageTweetsForUser1).containsExactly(tweet16, tweet13, tweet12, tweet11, tweet8);
        assertThat(nextTimelinePageTweetsForUser2).containsExactly(tweet17, tweet14, tweet13, tweet12, tweet9);
        assertThat(nextTimelinePageTweetsForUser3).containsExactly(tweet18, tweet15, tweet14, tweet13, tweet10);
        assertThat(nextTimelinePageTweetsForUser4).containsExactly(tweet19, tweet16, tweet15, tweet14, tweet11);
        assertThat(nextTimelinePageTweetsForUser5).containsExactly(tweet16, tweet15, tweet12, tweet11, tweet10);
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
        tweetRepository.save(tweet2);
        tweetRepository.save(tweet3);

        Tweet archivedTweet = tweetRepository.save(tweet2);

        //then
        //Optional<Tweet> foundTweet = tweetRepository.findById(archivedTweet.getId());
        Optional<Tweet> foundTweet = tweetRepository.findById(archivedTweet);

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
        //Optional<Tweet> foundTweet = tweetRepository.findById(tweet.getId());
        Optional<Tweet> foundTweet = tweetRepository.findById(tweet);

        System.out.println(savedTweet);
        System.out.println(foundTweet);

        //assertThat(foundTweet).isEqualTo(Optional.of(savedTweet));
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

        tweetRepository.deleteById(savedTweet2);

        //then
//        Optional<Tweet> foundTweet1 = tweetRepository.findById(savedTweet1.getId());
//        Optional<Tweet> foundTweet2 = tweetRepository.findById(savedTweet2.getId());
//        Optional<Tweet> foundTweet3 = tweetRepository.findById(savedTweet3.getId());

        Optional<Tweet> foundTweet1 = tweetRepository.findById(savedTweet1);
        Optional<Tweet> foundTweet2 = tweetRepository.findById(savedTweet2);
        Optional<Tweet> foundTweet3 = tweetRepository.findById(savedTweet3);

        assertThat(foundTweet1).isEqualTo(Optional.of(savedTweet1));

        assertThat(foundTweet2).isEqualTo(null);

        assertThat(foundTweet3).isEqualTo(Optional.of(savedTweet3));
    }

    @Test
    void cacheSave() {
        //given
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));

        userRepository.save(user);

        Tweet tweet = new Tweet("this be testing", user.getId());
        Tweet tweet2 = new Tweet("this be testing2", user.getId());
        Tweet tweet3 = new Tweet("this be testing3", user.getId());

        //when
        Tweet savedTweet = tweetRepository.save(tweet);
        Tweet savedTweet2 = tweetRepository.save(tweet2);
        Tweet savedTweet3 = tweetRepository.save(tweet3);

        //then
        Tweet cachedTweet1 = getCachedValue("tweets", savedTweet.getId(), Tweet.class);
        Tweet cachedTweet2 = getCachedValue("tweets", savedTweet2.getId(), Tweet.class);
        Tweet cachedTweet3 = getCachedValue("tweets", savedTweet3.getId(), Tweet.class);

        assertThat(cachedTweet1).isEqualTo(savedTweet);
        assertThat(cachedTweet2).isEqualTo(savedTweet2);
        assertThat(cachedTweet3).isEqualTo(savedTweet3);
    }

    private <T> T getCachedValue(String cacheName, Object key, Class<T> valueType) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                T cachedValue = valueType.cast(valueWrapper.get());
                System.out.println("Cached Value: " + cachedValue);
                return cachedValue;
            }
        }
        return null;
    }


    @Test
    void saveTweetInRedisAtSortedSet() {
        //given
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));
        userRepository.save(user);

        Tweet cachedTweet = new Tweet("haro, this be testing1", user.getId());
        Tweet cachedTweet2 = new Tweet("haro, this be testing2", user.getId());
        Tweet cachedTweet3 = new Tweet("haro, this be testing3", user.getId());

        //when
        tweetRepository.saveCache(cachedTweet);
        tweetRepository.saveCache(cachedTweet2);
        tweetRepository.saveCache(cachedTweet3);

        Set<Object> expectedTweetIds = Set.of(cachedTweet, cachedTweet2, cachedTweet3);

        //then
        Set<Object> tweetIds = redisTemplate.opsForZSet().range(user.getId(), 0, -1);
        assertThat(tweetIds.size()).isEqualTo(expectedTweetIds.size());
        assertThat(tweetIds).containsAll(expectedTweetIds);
    }

    @Test
    void findTweetInRedis() {
        //given
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));
        userRepository.save(user);

        Tweet cachedTweet = new Tweet("haro, this be testing1", user.getId());
        Tweet cachedTweet2 = new Tweet("haro, this be testing2", user.getId());
        Tweet cachedTweet3 = new Tweet("haro, this be testing3", user.getId());

        tweetRepository.saveCache(cachedTweet);
        tweetRepository.saveCache(cachedTweet2);
        tweetRepository.saveCache(cachedTweet3);

        //when
        Optional<Tweet> resultTweet = tweetRepository.findCache(cachedTweet);

        //then
        assertThat(resultTweet).isNotNull();
        assertThat(resultTweet).isPresent().contains(cachedTweet);
    }

    @Test
    void deleteTweetInRedis() {
        //given
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));
        userRepository.save(user);

        Tweet cachedTweet = new Tweet("haro, this be testing1", user.getId());
        Tweet cachedTweet2 = new Tweet("haro, this be testing2", user.getId());
        Tweet cachedTweet3 = new Tweet("haro, this be testing3", user.getId());

        tweetRepository.saveCache(cachedTweet);
        tweetRepository.saveCache(cachedTweet2);
        tweetRepository.saveCache(cachedTweet3);

        Set<Object> expectedTweets = Set.of(cachedTweet, cachedTweet3);

        //when
        tweetRepository.deleteCache(cachedTweet2);

        //then
        Set<Object> tweets = redisTemplate.opsForZSet().range(user.getId(), 0, -1);
        System.out.println("Tweets: " + tweets);
        assertThat(tweets).isNotNull();
        assertThat(tweets).isEqualTo(expectedTweets);
    }

    @Test
    void checkCacheTTL() {
        //given
        User user = new User("haro123", "haro@gmail.com", "b03b29", "haro", LocalDate.of(1999, 9, 9));
        userRepository.save(user);

        Tweet cachedTweet = new Tweet("haro, this be testing1", user.getId());

        //when
        tweetRepository.saveCache(cachedTweet);

        //then
        Long ttl = redisTemplate.getExpire(user.getId());
        assertThat(ttl).isEqualTo(3600);
    }

    @Test
    void callTimeLine() {
        // given
        User user1 = new User("user1", "user1@gmail.com", "AAAAAA", "user1ProfileName", LocalDate.of(1991, 1, 1));
        User user2 = new User("user2", "user2@gmail.com", "BBBBBB", "user2ProfileName", LocalDate.of(1992, 2, 2));
        User user3 = new User("user3", "user3@gmail.com", "CCCCCC", "user3ProfileName", LocalDate.of(1993, 3, 3));
        User user4 = new User("user4", "user4@gmail.com", "DDDDDD", "user4ProfileName", LocalDate.of(1994, 4, 4));
        User user5 = new User("user5", "user5@gmail.com", "EEEEEE", "user5ProfileName", LocalDate.of(1995, 5, 5));
        User user6 = new User("user6", "user6@gmail.com", "FFFFFF", "user6ProfileName", LocalDate.of(1996, 6, 6));
        User user7 = new User("user7", "user7@gmail.com", "GGGGGG", "user7ProfileName", LocalDate.of(1997, 7, 7));

        Follow follow1 = new Follow(user2.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS));
        Follow follow2 = new Follow(user3.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 2).truncatedTo(ChronoUnit.SECONDS));
        Follow follow3 = new Follow(user4.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 3).truncatedTo(ChronoUnit.SECONDS));
        Follow follow4 = new Follow(user5.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 4).truncatedTo(ChronoUnit.SECONDS));
        Follow follow5 = new Follow(user6.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 5).truncatedTo(ChronoUnit.SECONDS));
        Follow follow6 = new Follow(user7.getId(), user1.getId(), LocalDateTime.of(2023, 1, 1, 1, 1, 6).truncatedTo(ChronoUnit.SECONDS));

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);

        followRepository.save(follow1);
        followRepository.save(follow2);
        followRepository.save(follow3);
        followRepository.save(follow4);
        followRepository.save(follow5);
        followRepository.save(follow6);

        //Tweet cachedTweet = new Tweet("haro, this be testing1", user1.getId());
        Tweet cachedTweet2 = new Tweet("haro, this be testing2", user2.getId());
        Tweet cachedTweet3 = new Tweet("haro, this be testing3", user3.getId());
        Tweet cachedTweet4 = new Tweet("haro, this be testing4", user4.getId());
        Tweet cachedTweet5 = new Tweet("haro, this be testing5", user5.getId());
        Tweet cachedTweet6 = new Tweet("haro, this be testing6", user6.getId());
        Tweet cachedTweet7 = new Tweet("haro, this be testing7", user7.getId());

        //tweetRepository.saveCache(cachedTweet);
        tweetRepository.saveCache(cachedTweet2);
        tweetRepository.saveCache(cachedTweet3);
        tweetRepository.saveCache(cachedTweet4);
        tweetRepository.saveCache(cachedTweet5);
        tweetRepository.saveCache(cachedTweet6);
        tweetRepository.saveCache(cachedTweet7);
        Set<Object> expectedTweetIds = Set.of(cachedTweet2, cachedTweet3, cachedTweet4, cachedTweet5, cachedTweet6, cachedTweet7);
        Set<Object> resultTweets = new HashSet<>();

        //when
        //user1이 팔로우하는 팔로워 목록을 가져옴
        List<UserFollowDto> Users = followRepository.findFollowerById(user1.getId());
        for (Object user: Users) {
            Follow follow = (Follow) user;
            Set<Object> tweets = redisTemplate.opsForZSet().range(follow.getFollowerId(), 0, -1);
            for (Object tweetObj : tweets) {
                Tweet tweet = (Tweet) tweetObj;
                resultTweets.add(tweet);
            }
        }

        //then
        assertThat(expectedTweetIds).containsExactlyInAnyOrder(resultTweets.toArray());
    }

}
