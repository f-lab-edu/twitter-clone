package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * ERD reference: ERD_V01.02_E. equals(), hashCode() 시 필드 상호순환참조 문제 미연방지 위해 @Data 제거, @EqualsAndHashCode의 인자로 id값 설정.
 */
@Builder
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tweet {

    private final String id;

    private final String text;

    private final String userId;

    private final LocalDateTime createdAt;

    public Tweet(String text, String userId) {

        this.id = UUID.randomUUID().toString();

        this.text = text;

        this.userId = userId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
