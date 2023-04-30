package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Auto-generated fields: id. Reference: ERD_V01.02_D.
 */
@AllArgsConstructor
@Data
public class Tweet {

    private Long id;

    private String text;

    private Long userId;

    private LocalDateTime createdAt;

    public Tweet(String text, Long userId) {

        this.text = text;

        this.userId = userId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
