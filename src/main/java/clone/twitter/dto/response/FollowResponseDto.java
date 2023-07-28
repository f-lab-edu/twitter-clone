package clone.twitter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDto {

    private String followerId;

    private String followeeId;

    private boolean isFollowing;
}
