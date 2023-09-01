package clone.twitter.common;

import clone.twitter.domain.Follow;
import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DataGenerationHelper {

    public static final int NUMBER_OF_USERS = 20;

    public static final int NUMBER_OF_LIKE_TWEETS = 10;

    public static final int INDEX_OFFSET = 0;

    public static final LocalDateTime CREATED_AT_OFFSET = LocalDateTime.of(2023, 1, 1, 1, 1, 1).truncatedTo(ChronoUnit.SECONDS);

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

    public static UserSignUpRequestDto generateUserSignUpRequestDto(int index, LocalDateTime baseCreatedAt) {
        return UserSignUpRequestDto.builder()
                .username("username" + (index + 1))
                .email("user" + (index + 1) + "@twitter.com")
                .password("password" + (index + 1))
                .profileName("userProfileName" + (index + 1))
                .birthdate(baseCreatedAt.minusYears(20 + index).toLocalDate())
                .build();
    }

    public static Tweet generateTweet(int index, String composerId) {
        return Tweet.builder()
                .id("tweetId" + (index + 1)) // to be generated in the service layer
                .text("tweet text " + (index + 1))
                .userId(composerId)
                .createdAt(CREATED_AT_OFFSET.plusSeconds(index + 1)) // to be generated in the service layer
                .build();
    }

    public static TweetComposeRequestDto generateTweetComposeRequestDto(int index, String composerId) {
        return TweetComposeRequestDto.builder()
            .text("tweet text " + (index + 1))
            .userId(composerId)
            .build();
    }

    public static LikeTweet generateLikeTweet(String userId, String tweetId) {
        return LikeTweet.builder()
                .userId(userId)
                .tweetId(tweetId)
                //.createdAt(tweetCreatedAt.plusSeconds(index + 1)) // to be generated in the service layer
                .build();
    }

    public static Follow generateFollow(User follower, User followee) {
        LocalDateTime followCreatedAt = follower.getCreatedAt().isAfter(followee.getCreatedAt()) ? follower.getCreatedAt().plusSeconds(100): followee.getCreatedAt().plusSeconds(100);

        return Follow.builder()
                .followerId(follower.getId())
                .followeeId(followee.getId())
                //.createdAt(followCreatedAt) // to be generated in the service layer
                .build();
    }
}
