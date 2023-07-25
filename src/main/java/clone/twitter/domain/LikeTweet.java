package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * Reference: ERD_V01.02_E
 */
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"userId", "tweetId"})
public class LikeTweet {

    private final String tweetId;

    private final String userId;

    private final LocalDateTime createdAt;

    public LikeTweet(String tweetId, String userId) {

        this.tweetId = tweetId;

        this.userId = userId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
