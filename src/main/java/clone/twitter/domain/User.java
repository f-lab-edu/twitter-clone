package clone.twitter.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Auto-generated fields: id. Reference: ERD_V01.02_D.
 */
@AllArgsConstructor
@Data
public class User {

    private Long id;

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
    public User(String username,
        String email,
        String passwordHash,
        String profileName,
        LocalDate birthdate) {

        this.username = username;

        this.email = email;

        this.passwordHash = passwordHash;

        this.profileName = profileName;

        this.birthdate = birthdate;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
