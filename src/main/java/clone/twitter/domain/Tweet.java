package clone.twitter.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Reference: ERD_V01.02_E. Auto-generated fields: id.
 */
@AllArgsConstructor
@Data
public class Tweet {
    private String id;

    private String text;

    private String userId;

    private LocalDateTime createdAt;

    public Tweet(String text, String userId) {
        this.id = UUID.randomUUID().toString();

        this.text = text;

        this.userId = userId;

        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
