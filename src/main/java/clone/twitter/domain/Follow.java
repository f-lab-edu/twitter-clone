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
@EqualsAndHashCode(of = {"followerId", "followeeId"})
public class Follow {

    private final String followerId;

    private final String followeeId;

    private final LocalDateTime createdAt;

    public Follow(String followerId, String followeeId) {

        this.followerId = followerId;

        this.followeeId = followeeId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
