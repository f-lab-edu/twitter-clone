package clone.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeTweetResponseDto {

    private String tweetId;

    @JsonProperty("isLikedByUser")
    private boolean isLikedByUser;
}
