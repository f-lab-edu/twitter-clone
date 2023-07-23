package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TweetLoadRequestDto {

    private final String userIdOfViewer;

    private final LocalDateTime createdAtOfLastViewedTweet;
}
