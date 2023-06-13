package clone.twitter.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reference: ERD_V01.02_E. builder pattern 적용. Java Beans 규약 적용. equals(), hashCode() 시 필드 상호순환참조 문제 미연방지 위해 @Data 제거, @EqualsAndHashCode의 인자로 id값 설정.
 */
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class User {
    private String id;

    private String username;

    private String email;

    private String passwordHash;

    private String profileName;

    private LocalDate birthdate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * for signup API only
     */
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
