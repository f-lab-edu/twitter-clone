package clone.twitter.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TweetLoadRequestDto {
    String userIdOfViewer;

    LocalDateTime createdAtOfLastViewedTweet;
}
