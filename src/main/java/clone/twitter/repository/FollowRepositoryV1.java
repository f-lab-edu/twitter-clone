package clone.twitter.repository;

import clone.twitter.domain.Follow;
import clone.twitter.repository.dto.UserFollowDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FollowRepositoryV1 implements FollowRepository{

    private final FollowMapper followMapper;

    private static final int FOLLOW_LOAD_LIMIT = 3;

    @Override
    public void save(Follow follow) {
        followMapper.follow(follow);
    }

    @Override
    public void delete(String followerId, String followeeId) {
        followMapper.unfollow(followerId, followeeId);
    }

    @Override
    public List<UserFollowDto> findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(String followerId, String followeeId, LocalDateTime createdAt) {
        return followMapper.findFollowList(followerId, followeeId, createdAt, FOLLOW_LOAD_LIMIT);
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId) {
        return followMapper.findByIds(followerId, followeeId);
    }

    @Override
    public List<String> findFollowerIdsByFolloweeId(String followeeId) {
        return followMapper.findFollowerIdsByFolloweeId(followeeId);
    };
}
