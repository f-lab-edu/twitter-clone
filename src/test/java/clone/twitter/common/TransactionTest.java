package clone.twitter.common;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.service.LikeTweetService;
import clone.twitter.service.TweetService;
import clone.twitter.service.UserService;
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
public class TransactionTest extends DataGenerationHelper{

    @Autowired
    UserService userService;

    @Autowired
    TweetService tweetService;

    @Autowired
    LikeTweetService likeTweetService;

    @Test
    @DisplayName("Spring @Transactional 설정 검증 테스트. INSERT 수행 -> 의도적 예외 호출 -> 롤백(-> DB 확인).")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    void springTransactionAopTest() {
        // user 생성 및 쓰기.
        // i.e. user의 id, createdAt, updatedAt 필드값은 userService.signUp() 호출시 생성. 테스트 전반에서 DB와 데이터와 일치하려면 해당 생성된 값 활용 필요.
        User userObj1 = generateUser(INDEX_OFFSET, CREATED_AT_OFFSET);
        User userObj2 = generateUser(INDEX_OFFSET + 1, CREATED_AT_OFFSET);

        // 로직 자체가
        Optional<UserResponseDto> optionalUserResponseDto1 = userService.signUp(userObj1);
        Optional<UserResponseDto> optionalUserResponseDto2 = userService.signUp(userObj2);

        // user 쓰기 실패시 테스트 중단
        Assertions.assertThat(optionalUserResponseDto1.isPresent()).isTrue();
        Assertions.assertThat(optionalUserResponseDto2.isPresent()).isTrue();

        // (Additional test, not required)
        // tweet 생성 및 쓰기
        Tweet tweetObj = generateTweet(INDEX_OFFSET, optionalUserResponseDto1.get().getUserId());
        Tweet tweet = tweetService.composeTweet(tweetObj);

        // (Additional test, not required)
        // likeTweet 생성 및 쓰기
        LikeTweet likeTweet = generateLikeTweet(optionalUserResponseDto1.get().getUserId(), tweet.getId());
        likeTweetService.likeTweet(tweet.getId(), optionalUserResponseDto1.get().getUserId());

        // 의도적 예외 호출 -> 롤백 -> 트랜잭션이 롤백되어 DB 각 테이블에 데이터가 남아있지 않은지 수동 확인
        try {
            throw new Exception("의도된 예외 발생! 테스트 종료.");
        } catch (Exception e) {
            log.info("Caught exception: {}", e.getMessage());
        }
    }
}
