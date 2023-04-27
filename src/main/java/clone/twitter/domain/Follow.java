package clone.twitter.domain;

import java.time.LocalDateTime;

/**
 * - auto-generated fields: createdAt
 * - reference: ERD_V01.02_C
 */
public class Follow {
    private final Long followerId;
    private final Long followeeId;

    private LocalDateTime createdAt;

    public Follow(Long follower_id, Long followee_id) {
        this.followerId = follower_id;
        this.followeeId = followee_id;
    }

    public Long getFollowerId() {return followerId;}
    public Long getFolloweeId() {return followeeId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
