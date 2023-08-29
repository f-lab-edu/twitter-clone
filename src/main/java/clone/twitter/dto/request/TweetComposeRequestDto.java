package clone.twitter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetComposeRequestDto {

    @NotBlank(message = "Tweet text cannot be blank")
    @Size(max = 280, message = "Tweet text must be less than 280 characters")
    @Pattern(regexp="^(?!\\s*$).+", message="Tweet text must not start with blank space")
    private String text;
}
