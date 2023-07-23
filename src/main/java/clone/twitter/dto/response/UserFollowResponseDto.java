package clone.twitter.dto.response;

import clone.twitter.domain.Follow;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserFollowResponseDto {

    private final  UserResponseDto userResponseDto;

    private final  Follow follow;

    @JsonProperty("isFollowing")
    private final  boolean isFollowing;
}
