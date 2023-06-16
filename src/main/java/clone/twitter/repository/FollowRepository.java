package clone.twitter.repository;

import clone.twitter.domain.Follow;
import clone.twitter.repository.dto.UserFollowDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FollowRepository {
    void follow(Follow follow);

    void unfollow(Follow follow);

    List<UserFollowDto> findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(String followerId, String followeeId, LocalDateTime createdAt);

    Optional<Follow> findByIds(String followerId, String followeeId);
}
