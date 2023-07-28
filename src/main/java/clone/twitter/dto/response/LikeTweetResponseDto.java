package clone.twitter.dto.response;

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

    private boolean isLikedByUser;
}
