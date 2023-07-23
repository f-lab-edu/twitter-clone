package clone.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LikeTweetResponseDto {

    private final  String tweetId;

    @JsonProperty("isLikedByUser")
    private final  boolean isLikedByUser;
}
