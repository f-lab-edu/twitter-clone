package clone.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowResponseDto {

    private final  String followerId;

    private final  String followeeId;

    @JsonProperty("isFollowing")
    private final  boolean isFollowing;
}
