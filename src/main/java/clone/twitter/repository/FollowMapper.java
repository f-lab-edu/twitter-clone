package clone.twitter.repository;

import clone.twitter.domain.Follow;
import clone.twitter.domain.User;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowMapper {
    void follow(Follow follow);

    void unfollow(Follow follow);

    List<User> findInitialFollowingList(@Param("followerId") String followerId, @Param("limit") int limit);

    List<User> findNextFollowingList(@Param("followerId") String followerId, @Param("followeeId") String followeeId, @Param("limit") int limit);

    List<User> findInitialFollowedList(@Param("followeeId") String followeeId, @Param("limit") int limit);

    List<User> findNextFollowedList(@Param("followeeId") String followeeId, @Param("followerId") String followerId, @Param("limit") int limit);

    Optional<Follow> findByIds(@Param("followerId") String followerId, @Param("followeeId") String followeeId);
}
