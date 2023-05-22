package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reference: ERD_V01.02_E
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Follow {
    private String followerId;

    private String followeeId;

    private LocalDateTime createdAt;

    public Follow(String followerId, String followeeId) {
        this.followerId = followerId;

        this.followeeId = followeeId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
