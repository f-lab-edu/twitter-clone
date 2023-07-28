package clone.twitter.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tweet {

    private String id;

    private String text;

    private String userId;

    private LocalDateTime createdAt;
}

/*
 - 객체 내 필드로 다른 객체를 포함하는 경우 필드 상호순환참조 문제가 생길 수 있습니다. 이를 미연에 방지하고자
 domain 객체 내 @EqualsAndHashCode의 인자를 id값으로 지정하였습니다.
 */