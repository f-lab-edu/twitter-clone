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

/*
 Lombok 라이브러리 용처 설명
 - `@Getter`: 객체 정보를 받아올 수 있도록 합니다.
 - `@Builder`: 보다 직관직이고 명시적인 방식으로 객체를 생성할 수 있습니다. `@...Constructor` 등을 통해
 허용한 생성자 형식대로 사용할 수 있습니다.
 - `@NoArgsConstructor`: 모든 필드가 빈 객체 생성을 허용하며 객체 역질렬화 시 주로 사용됩니다.
 - `@AllArgsConstructor`: 모든 필드에 값이 할당되는 객체 생성을 허용합니다.
 */
