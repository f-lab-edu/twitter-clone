package clone.twitter.domain;

import java.time.LocalDateTime;

/**
 * - auto-generated fields: createdAt
 * - reference: ERD_V01.02_C
 */
public class Like {
    private final Long tweetId;
    private final Long userId;

    private LocalDateTime createdAt;

    public Like(Long tweetId, Long userId) {
        this.tweetId = tweetId;
        this.userId = userId;
    }

    public Long getTweetId() {return tweetId;}
    public Long getUserId() {return userId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
