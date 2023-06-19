package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowRequestDto {
    @Nullable
    String followerId;

    @Nullable
    String followeeId;

    @Nullable
    LocalDateTime createdAt;
}
