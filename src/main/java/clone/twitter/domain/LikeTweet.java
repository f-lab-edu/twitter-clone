package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Reference: ERD_V01.02_D
 */
@AllArgsConstructor
@Data
public class LikeTweet {

    private Long tweetId;

    private Long userId;

    private LocalDateTime createdAt;

    public LikeTweet(Long tweetId, Long userId) {

        this.tweetId = tweetId;

        this.userId = userId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
