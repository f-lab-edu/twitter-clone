package clone.twitter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInRequestDto {

    @Email(message = "유효한 이메일을 입력해 주세요.")
    private String email;

    @Size(min = 8, max = 30, message = "비밀번호를 8자 이상 30자 이하로 입력해주세요.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}

/*
 Lombok 라이브러리 용처 설명
 - `@Getter`: 객체 정보를 받아올 수 있도록 합니다.
 - `@Builder`: 보다 직관직이고 명시적인 방식으로 객체를 생성할 수 있습니다. `@...Constructor` 등을 통해
 허용한 생성자 형식대로 사용할 수 있습니다.
 - `@NoArgsConstructor`: 모든 필드가 빈 객체 생성을 허용하며 객체 역질렬화 시 주로 사용됩니다.
 - `@AllArgsConstructor`: 모든 필드에 값이 할당되는 객체 생성을 허용합니다.
 */
