package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reference: ERD_V01.02_E
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"userId", "tweetId"})
public class LikeTweet {
    private String tweetId;

    private String userId;

    private LocalDateTime createdAt;

    public LikeTweet(String tweetId, String userId) {
        this.tweetId = tweetId;

        this.userId = userId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
