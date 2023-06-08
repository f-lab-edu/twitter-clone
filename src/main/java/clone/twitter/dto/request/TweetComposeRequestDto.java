package clone.twitter.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TweetComposeRequestDto {
    @NotBlank(message = "Tweet text cannot be blank")
    @Size(max = 280, message = "Tweet text must be less than 280 characters")
    @Pattern(regexp="^(?!\\s*$).+", message="Tweet text must not start with blank space")
    private String text;

    @NotBlank
    private String userId;
}
