package clone.twitter.util;

import clone.twitter.dto.request.UserFollowRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/*
 * FollowUsersRequestDto의 followerId, followeeId 필드 둘 중 하나가 비어있음을 검증
 */
@Component
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
