package clone.twitter.dto.request;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TweetPostRequestDto {
    @NotEmpty
    private String text;

    @NotEmpty
    private String userId;
}
