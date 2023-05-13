package clone.twitter.repository;

import clone.twitter.domain.Follow;
import clone.twitter.domain.User;
import java.util.List;
import java.util.Optional;

public interface FollowRepository {
    void follow(Follow follow);

    void unfollow(Follow follow);

    List<User> findByFollowerIdOrderByCreatedAtDesc(String followerId);

    List<User> findNextByFollowerIdOrderByCreatedAtDesc(String followerId, String followeeId);

    List<User> findByFolloweeIdOrderByCreatedAtDesc(String followeeId);

    List<User> findNextByFolloweeIdOrderByCreatedAtDesc(String followeeId, String followerId);

    Optional<Follow> findByIds(String followerId, String followeeId);
}
