package clone.twitter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserSigninRequestDto {
    @Size(min = 8, max = 30, message = "Username must be between 8 and 30 characters")
    private String username;

    @Email(message = "Invalid email")
    private String email;

    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @NotBlank(message = "Tweet text cannot be blank")
    private String password;
}
