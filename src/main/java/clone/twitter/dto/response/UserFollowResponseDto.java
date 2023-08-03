package clone.twitter.dto.response;

import clone.twitter.domain.Follow;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowResponseDto {

    private UserResponseDto userResponseDto;

    private Follow follow;

    @JsonProperty("isFollowing")
    private boolean isFollowing;
}

/*
 `isFollowing`필드를 `@JsonProperty("isFollowing")와 같이 명시한 이유
 - `is...`의 형태가 Jackson의 기본 네이밍 문법/규칙이기 때문에, 직렬화/역직렬화 시
 해당 네이밍 규칙으로 쓰여진 필드를 필드로서 인식하지 않아 필드값이 무시되기 때문입니다.

 Lombok 라이브러리 용처 설명
 - `@Getter`: 객체 정보를 받아올 수 있도록 합니다.
 - `@Builder`: 보다 직관직이고 명시적인 방식으로 객체를 생성할 수 있습니다. `@...Constructor` 등을 통해
 허용한 생성자 형식대로 사용할 수 있습니다.
 - `@NoArgsConstructor`: 모든 필드가 빈 객체 생성을 허용하며 객체 역질렬화 시 주로 사용됩니다.
 - `@AllArgsConstructor`: 모든 필드에 값이 할당되는 객체 생성을 허용합니다.
 */
