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

    /**
     * pagination 시 한 번에 로드되는 팔로우 개수을 지정합니다.
     */
    private static final int FOLLOW_LOAD_LIMIT = 3;

    /**
     * 다른 유저를 팔로우 합니다.
     * @param follow 팔로우를 실행하는 주체 유저의 id와 타겟 유저의 id, 팔로우 실행 시간 정보가 담긴 follow 객체
     */
    @Override
    public void save(Follow follow) {
        followMapper.follow(follow);
    }

    /**
     * 팔로우했던 다른 유저에 대해 팔로우를 취소합니다.
     * @param follow 팔로우를 실행했던 주체 유저의 id와 타겟 유저의 id, 팔로우 실행 시간 정보가 담긴 follow 객체
     */
    @Override
    public void delete(Follow follow) {
        followMapper.unfollow(follow);
    }

    /**
     * 팔로잉 또는 팔로워 목록을 팔로우 발생 시간의 내림차순으로 불러옵니다.
     * @param followerId 팔로잉 목록을 불러올 때 팔로우의 주체가 되는 유저의 id(프로필 페이지상 프로필주인의 아이디). 팔로워 목록 조회시 null 입력.
     * @param followeeId 팔로워 목록을 불러올 때 팔로우의 타겟이 되는 유저의 id(프로필 페이지상 프로필주인의 아이디) 팔로잉 목록 조회시 null 입력.
     * @param createdAt 불러온 팔로우 목록 중 페이지 마지막 팔로우의 발생 시간 정보. 목록의 페이지를 처음 불러올 시 null 입력. 추가 페이지 로드 시 이 시간을 기준으로 팔로우 목록을 불러옵니다.
     * @return 유저와 팔로우 관계 정보를 순서대로 담은 UserFollowDto 객체의 리스트 반환
     */
    @Override
    public List<UserFollowDto> findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(String followerId, String followeeId, LocalDateTime createdAt) {
        return followMapper.findFollowList(followerId, followeeId, createdAt, FOLLOW_LOAD_LIMIT);
    }

    /**
     * 운영용 메서드
     * @param followerId 팔로우하는 유저의 id
     * @param followeeId 팔로우 타겟 유저의 id
     * @return 팔로우 관계 정보와 일치하는 팔로우 객체 반환
     */
    public Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId) {
        return followMapper.findByIds(followerId, followeeId);
    };
}
