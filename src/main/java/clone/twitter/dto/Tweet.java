package clone.twitter.dto;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@NoArgsConstructor
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

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
