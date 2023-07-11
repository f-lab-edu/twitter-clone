package clone.twitter.common;

import clone.twitter.domain.Follow;
import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DataGenerationHelper {

    public static final int NUMBER_OF_USERS = 20;

    public static final int NUMBER_OF_LIKE_TWEETS = 10;

    public static final int BEGINNING_INDEX_OF_STREAM_RANGE = 0;

    public static final LocalDateTime BASE_CREATED_AT = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

    public static User generateUser(int index, LocalDateTime baseCreatedAt) {
        return User.builder()
                .id("user" + (index + 1))
                .username("username" + (index + 1))
                .email("user" + (index + 1) + "@twitter.com")
                .passwordHash("password" + (index + 1))
                .profileName("userProfileName" + (index + 1))
                .birthdate(baseCreatedAt.minusYears(20 + index).toLocalDate())
                .createdAt(baseCreatedAt.plusSeconds(index))
                .updatedAt(baseCreatedAt.plusSeconds(index))
                .build();
    }

    public static Tweet generateTweet(int index, String composerId, LocalDateTime baseCreatedAt) {
        return Tweet.builder()
                .id("tweetId" + (index + 1))
                .text("tweet text " + (index + 1))
                .userId(composerId)
                .createdAt(baseCreatedAt.plusSeconds(index + 1))
                .build();
    }

    public static LikeTweet generateLikeTweet(int index, String userId, String tweetId, LocalDateTime tweetCreatedAt) {
        return LikeTweet.builder()
                .userId("user" + (index + 1))
                .tweetId(tweetId) // fixed target of like-tweet
                .createdAt(tweetCreatedAt.plusSeconds(index + 1))
                .build();
    }

    public static Follow generateFollow(User follower, User followee) {
        LocalDateTime followCreatedAt = follower.getCreatedAt().isAfter(followee.getCreatedAt()) ? follower.getCreatedAt().plusSeconds(100): followee.getCreatedAt().plusSeconds(100);

        return Follow.builder()
                .followerId(follower.getId())
                .followeeId(followee.getId())
                .createdAt(followCreatedAt)
                .build();
    }
}
