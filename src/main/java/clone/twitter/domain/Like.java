package clone.twitter.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Auto-generated fields: createdAt. Reference: ERD_V01.02_C
 */
@AllArgsConstructor
@Data
public class Like {

    private Long tweetId;

    private Long userId;

    private LocalDateTime createdAt;

    public Like(Long tweetId, Long userId) {
        this.tweetId = tweetId;
        this.userId = userId;
    }
}
