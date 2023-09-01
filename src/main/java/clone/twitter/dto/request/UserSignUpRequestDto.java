package clone.twitter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequestDto {

    @Size(min = 8, max = 30, message = "username을 8자 이상 30자 이하로 입력해 주세요.")
    private String username;

    @Email(message = "이메일 형식이 유효하지 않습니다.")
    private String email;

    @Size(min = 8, max = 30, message = "비밀번호를 8자 이상 30자 이하로 입력해주세요.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "프로필 이름을 입력해 주세요.")
    private String profileName;

    @NotNull(message = "생년월일을 입력해 주세요.")
    private LocalDate birthdate;
}
