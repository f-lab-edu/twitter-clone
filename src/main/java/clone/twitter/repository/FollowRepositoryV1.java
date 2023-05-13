package clone.twitter.repository;

import clone.twitter.domain.Follow;
import clone.twitter.domain.User;
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
    public void follow(Follow follow) {
        followMapper.follow(follow);
    }

    /**
     * 팔로우했던 다른 유저에 대해 팔로우를 취소합니다.
     * @param follow 팔로우를 실행했던 주체 유저의 id와 타겟 유저의 id, 팔로우 실행 시간 정보가 담긴 follow 객체
     */
    @Override
    public void unfollow(Follow follow) {
        followMapper.unfollow(follow);
    }

    /**
     * 팔로잉 목록에 처음 접근했을 시 고정된 수의 팔로우 타겟 유저 정보를 팔로우 생성시간 기준 내림차순으로 불러옵니다.
     * @param followerId 팔로우를 실행한 주체 유저의 id(프로필 페이지상 프로필주인의 아이디)
     * @return 유저를 팔로우중인 유저들의 목록을 정해진 갯수만큼 팔로우 생성시간 기준 내림차순으로 불러옵니다.
     */
    @Override
    public List<User> findByFollowerIdOrderByCreatedAtDesc(String followerId) {
        return followMapper.findInitialFollowingList(followerId, FOLLOW_LOAD_LIMIT);
    }

    /**
     * 팔로잉 목록을 스크롤 다운하여 이전에 불러온 팔로우 타겟 유저 목록의 마지막 유저에 도달했을 때, 고정된 수의 팔로워 목록을 추가로 불러옵니다.
     * @param followerId 팔로우를 실행한 주체 유저의 id(프로필 페이지상 프로필주인의 아이디)
     * @param followeeId 이전에 불러온 타겟 팔로워 목록 중 마지막 순번 유저의 id
     * @return 팔로우 타겟 유저들의 목록중 안불러온 다음 목록을 정해진 갯수만큼 팔로우 생성시간 기준 내림차순으로 불러옵니다.
     */
    @Override
    public List<User> findNextByFollowerIdOrderByCreatedAtDesc(String followerId, String followeeId) {
        return followMapper.findNextFollowingList(followerId, followeeId, FOLLOW_LOAD_LIMIT);
    }

    /**
     * 팔로워 목록에 처음 접근했을 시 고정된 수의 유저 정보를 팔로우 생성시간 기준 내림차순으로 불러옵니다.
     * @param followeeId 팔로우의 타겟이 되는 유저의 id(프로필 페이지상 프로필 주인의 id)
     * @return 유저를 팔로우중인 유저들의 목록을 정해진 갯수만큼 팔로우 생성시간 기준 내림차순으로 불러옵니다.
     */
    @Override
    public List<User> findByFolloweeIdOrderByCreatedAtDesc(String followeeId) {
        return followMapper.findInitialFollowedList(followeeId, FOLLOW_LOAD_LIMIT);
    }

    /**
     * 팔로워 목록을 스크롤 다운하여 이전에 불러온 팔로워 유저 목록의 마지막 유저에 도달했을 때, 고정된 수의 팔로워 목록을 추가로 불러옵니다.
     * @param followeeId 팔로우의 타겟이 되는 유저의 id(프로필 페이지상 프로필주인의 아이디)
     * @param followerId 이전에 불러온 팔로워 목록 중 마지막 순번 유저의 id
     * @return 타겟 유저를 팔로우중인 유저들의 목록중 안불러온 다음 목록을 정해진 갯수만큼 팔로우 생성시간 기준 내림차순으로 불러옵니다.
     */
    @Override
    public List<User> findNextByFolloweeIdOrderByCreatedAtDesc(String followeeId, String followerId) {
        return followMapper.findNextFollowedList(followeeId, followerId, FOLLOW_LOAD_LIMIT);
    }

    /**
     * 운영용 메서드
     * @param followerId 팔로우하는 유저의 id
     * @param followeeId 팔로우 타겟 유저의 id
     * @return 팔로우 관계 정보와 일치하는 팔로우 객체 반환
     */
    public Optional<Follow> findByIds(String followerId, String followeeId) {
        return followMapper.findByIds(followerId, followeeId);
    };
}
