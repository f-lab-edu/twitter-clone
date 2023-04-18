package clone.twitter.v0.domain;

import clone.twitter.v0.dto.request.TweetRequestDto;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Builder
@Getter
@AllArgsConstructor
@Entity
//public class Tweet extends Timestamped {
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tweetId;

    @Column(nullable = false)
    private final String memberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private final String content;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}
