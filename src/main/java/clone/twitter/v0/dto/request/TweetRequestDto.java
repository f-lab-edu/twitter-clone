package clone.twitter.v0.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestDto {
    private Long tweetId;
    private String memberId;
    private String content;
}
