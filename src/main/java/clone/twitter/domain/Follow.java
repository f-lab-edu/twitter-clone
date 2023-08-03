package clone.twitter.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"followerId", "followeeId"})
public class Follow {

    private String followerId;

    private String followeeId;

    private LocalDateTime createdAt;
}

/*
 Lombok 라이브러리 용처 설명
 - `@Getter`: 객체 정보를 받아올 수 있도록 합니다.
 - `@Builder`: 보다 직관직이고 명시적인 방식으로 객체를 생성할 수 있습니다. `@...Constructor` 등을 통해
 허용한 생성자 형식대로 사용할 수 있습니다.
 - `@NoArgsConstructor`: 모든 필드가 빈 객체 생성을 허용하며 객체 역질렬화 시 주로 사용됩니다.
 - `@AllArgsConstructor`: 모든 필드에 값이 할당되는 객체 생성을 허용합니다.
 - `@EqualsAndHashCode`: 객체의 논리 동치성을 확인하는 기준을 명시하며, 객체별로 유니크한 필드인 동시에
 다른 객체를 참조하지 않는 필드를 비교 기준으로 지정함으로써 비교시 객체 상호순환 참조를 방지할 수 있습니다.
 */
