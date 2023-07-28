package clone.twitter.util;

import clone.twitter.dto.request.UserFollowRequestDto;
import org.springframework.validation.Errors;

public class UserFollowRequestDtoValidator {

    public static void validate(UserFollowRequestDto followUsersRequestDto, Errors errors) {
        if (followUsersRequestDto.getFollowerId() == null && followUsersRequestDto.getFolloweeId() == null) {
            errors.reject("bothNull", "Both followerId and followeeId cannot be null");
        }
        if (followUsersRequestDto.getFollowerId() != null && followUsersRequestDto.getFolloweeId() != null) {
            errors.reject("noneNull", "Both followerId and followeeId cannot be non-null");
        }
    }
}
