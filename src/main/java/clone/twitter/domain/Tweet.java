package clone.twitter.domain;

import java.time.LocalDateTime;

/**
 * - auto-generated fields: id, createdAt
 * - reference: ERD_V01.02_C
 */
public class Tweet {
    private Long id;

    private final String text;
    private final String userId;
    private LocalDateTime createdAt;

    public Tweet(String text, String userId) {
        this.text = text;
        this.userId = userId;
    }

    public Long getId() {return id;}
    public String getText() {return text;}
    public String getUserId() {return userId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
