package clone.twitter.v0.dto.response;

import clone.twitter.v0.domain.Tweet;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 'likes' 필드 및 관련 기능 이후 추가
 */

@Builder
@Getter
@AllArgsConstructor
public class TweetResponseDto<T> {
    private final Long tweetId;
    private final String memberId;
    private final String content;
    private final LocalDateTime createdAt;
    // private int likes;
}
