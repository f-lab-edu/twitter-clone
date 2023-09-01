package clone.twitter.common;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
import clone.twitter.service.LikeTweetService;
import clone.twitter.service.TweetService;
import clone.twitter.service.UserServiceWithSession;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
public class TransactionTest extends DataGenerationHelper {

    @Autowired
    UserServiceWithSession userService;

    @Autowired
    TweetService tweetService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    LikeTweetService likeTweetService;

    @Test
    @DisplayName("Spring @Transactional 설정 검증 테스트. INSERT 수행 -> 의도적 예외 호출 -> 롤백(-> DB 확인).")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    void springTransactionAopTest() {
        // user, tweet, like-tweet 생성 및 DB 저장
        // i.e. user의 id, createdAt, updatedAt 필드값은 userService.signUp() 호출시 생성. 테스트 전반에서 DB와 데이터와 일치하려면 해당 생성된 값 활용 필요.
        UserSignUpRequestDto userSignUpRequestObj1 = generateUserSignUpRequestDto(
            INDEX_OFFSET, CREATED_AT_OFFSET);
        UserSignUpRequestDto userSignUpRequestObj2 = generateUserSignUpRequestDto(
            INDEX_OFFSET + 1, CREATED_AT_OFFSET);

        // user 회원가입, DB 저장
        userService.signUp(userSignUpRequestObj1);
        userService.signUp(userSignUpRequestObj2);

        // 저장한 user 읽기
        Optional<User> optionalUSer1 = userRepository.findByUsername(
            userSignUpRequestObj1.getUsername());
        Optional<User> optionalUSer2 = userRepository.findByUsername(
            userSignUpRequestObj1.getUsername());

        // user 쓰기 실패시 테스트 중단
        Assertions.assertThat(optionalUSer1.isPresent()).isTrue();
        Assertions.assertThat(optionalUSer2.isPresent()).isTrue();

        // tweet 작성 및 DB 저장
        TweetComposeRequestDto tweetComposeRequestDtoObj = generateTweetComposeRequestDto(
            INDEX_OFFSET, optionalUSer1.get().getId());

        Tweet tweet = tweetService.composeTweet(tweetComposeRequestDtoObj);

        // likeTweet 생성 및 DB 저장
        LikeTweet likeTweet = generateLikeTweet(optionalUSer1.get().getId(), tweet.getId());

        likeTweetService.likeTweet(tweet.getId(), optionalUSer1.get().getId());

        // 의도적 예외 호출 -> 롤백 -> 트랜잭션이 롤백되어 DB 각 테이블에 데이터가 남아있지 않은지 수동 확인
        try {
            throw new Exception("의도된 예외 발생! 테스트 종료");
        } catch (Exception e) {
            log.info("Caught exception: {}", e.getMessage());
        }
    }
}
