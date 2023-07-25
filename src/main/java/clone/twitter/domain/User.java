package clone.twitter.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * Reference: ERD_V01.02_E. equals(), hashCode() 시 필드 상호순환참조 문제 미연방지 위해 @Data 제거, @EqualsAndHashCode의 인자로 id값 설정.
 */
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    private final String id;

    private final String username;

    private final String email;

    private final String passwordHash;

    private final String profileName;

    private final LocalDate birthdate;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public User(String username, String email, String passwordHash, String profileName, LocalDate birthdate) {

        this.id = UUID.randomUUID().toString();

        this.username = username;

        this.email = email;

        this.passwordHash = passwordHash;

        this.profileName = profileName;

        this.birthdate = birthdate;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
