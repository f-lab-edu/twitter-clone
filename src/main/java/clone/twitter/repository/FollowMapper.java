package clone.twitter.repository;

import clone.twitter.domain.Follow;
import clone.twitter.repository.dto.UserFollowDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowMapper {

    void follow(Follow follow);

    void unfollow(Follow follow);

    List<UserFollowDto> findFollowList(@Param("followerId") String followerId, @Param("followeeId") String followeeId, @Param("createdAt") LocalDateTime createdAt, @Param("limit") int limit);

    Optional<Follow> findByIds(@Param("followerId") String followerId, @Param("followeeId") String followeeId);
}
