package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowRequestDto {

    @Nullable
    private String followerId;

    @Nullable
    private String followeeId;

    @Nullable
    private LocalDateTime createdAtOfUserLastOnList;
}
