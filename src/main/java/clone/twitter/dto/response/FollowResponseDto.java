package clone.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDto {

    private String followerId;

    private String followeeId;

    @JsonProperty("isFollowing")
    private boolean isFollowing;
}

/*
 `isFollowing`필드를 `@JsonProperty("isFollowing")와 같이 명시한 이유
 - `is...`의 형태가 Jackson의 기본 네이밍 문법/규칙이기 때문에, 직렬화/역직렬화 시
 해당 네이밍 규칙으로 쓰여진 필드를 필드로서 인식하지 않아 필드값이 무시되기 때문입니다.
*/
