package clone.twitter.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Auto-generated fields: id, createdAt. Reference: ERD_V01.02_C.
 */
@AllArgsConstructor
@Data
public class Tweet {

    private Long id;

    private String text;

    private String userId;

    private LocalDateTime createdAt;

    public Tweet(String text, String userId) {
        this.text = text;
        this.userId = userId;
    }
}
