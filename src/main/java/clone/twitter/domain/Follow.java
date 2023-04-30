package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Reference: ERD_V01.02_D.
 */
@AllArgsConstructor
@Data
public class Follow {

    private Long followerId;

    private Long followeeId;

    private LocalDateTime createdAt;

    public Follow(Long follower_id, Long followee_id) {

        this.followerId = follower_id;

        this.followeeId = followee_id;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
