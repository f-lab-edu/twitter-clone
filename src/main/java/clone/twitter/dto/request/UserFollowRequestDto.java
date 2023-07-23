package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@Builder
@AllArgsConstructor
public class UserFollowRequestDto {

    @Nullable
    private final String followerId;

    @Nullable
    private final String followeeId;

    @Nullable
    private final LocalDateTime createdAtOfUserLastOnList;
}
