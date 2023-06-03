package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestDto {
    // 요청을 한 유저의 id. 다음 api 버전에서 username 필드로 대체하도록 리팩토링할 예정입니다.
    private String userId;

    private LocalDateTime createdAtOfTweet;
}
