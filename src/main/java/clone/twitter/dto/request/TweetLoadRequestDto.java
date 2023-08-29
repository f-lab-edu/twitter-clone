package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetLoadRequestDto {

    private String userIdOfViewer;

    private LocalDateTime createdAtOfLastViewedTweet;
}
