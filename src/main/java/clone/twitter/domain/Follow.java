package clone.twitter.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"followerId", "followeeId"})
public class Follow {

    private String followerId;

    private String followeeId;

    private LocalDateTime createdAt;
}
