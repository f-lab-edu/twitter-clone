package clone.twitter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignInRequestDto {
    //
    // 보류
    // @Size(min = 8, max = 30, message = "username을 8자 이상 30자 이하로 입력해 주세요.")
    // private String username;

    @Email(message = "유효한 이메일을 입력해 주세요.")
    private final  String email;

    @Size(min = 8, max = 30, message = "비밀번호를 8자 이상 30자 이하로 입력해주세요.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final  String password;
}
