package clone.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDto {
    private String followerId;

    private String followeeId;

    @JsonProperty("isFollowing")
    private boolean isFollowing;
}
