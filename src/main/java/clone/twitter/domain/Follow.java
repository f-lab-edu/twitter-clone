package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class Follow {
    private String followerId;

    private String followeeId;

    private LocalDateTime createdAt;

    public Follow(String followerId, String followeeId) {
        this.followerId = followerId;

        this.followeeId = followeeId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * follow 객체의 논리 동치성 검증에 있어 followerId와 followeeId 필드의 순서 요소 반영을 위해 별도의 equals()와 hashCode() 구현 및 override(lombok을 활용한 @EqualsAndHashCode(of = {"followerId", "followeeId"})와 같은 코드는 필드의 순서요소를 고려하지 않음)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Follow follow = (Follow) o;

        if (followerId != null ? !followerId.equals(follow.followerId) : follow.followerId != null) return false;
        return followeeId != null ? followeeId.equals(follow.followeeId) : follow.followeeId == null;
    }

    @Override
    public int hashCode() {
        int result = followerId != null ? followerId.hashCode() : 0;
        result = 31 * result + (followeeId != null ? followeeId.hashCode() : 0);
        return result;
    }
}
