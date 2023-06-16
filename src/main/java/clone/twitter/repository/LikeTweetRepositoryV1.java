package clone.twitter.repository;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LikeTweetRepositoryV1 implements LikeTweetRepository {
    private final LikeTweetMapper likeTweetMapper;

    /**
     * 타겟 트윗에 대한 특정 유저의 좋아요 정보를 저장합니다.
     * @param likeTweet 트윗의 id, 유저의 id 등 좋아요 관련 정보를 담은 객체
     */
    @Override
    public void save(LikeTweet likeTweet) {
        likeTweetMapper.likeTweet(likeTweet);
    }

    /**
     * 특정 트윗에 대한 특정 유저의 좋아요 정보를 삭제합니다.
     * @param tweetId 좋아요의 타겟 트윗 id
     * @param userId 타겟 트윗에 좋아요를 표시한 유저 id
     */
    @Override
    public void deleteByTweetIdAndUserId(String tweetId, String userId) {
        likeTweetMapper.unlikeTweet(tweetId, userId);
    }

    /**
     * 특정 트윗에 좋아요를 표시한 유저들의 정보를 한계설정값의 수 만큼 조회합니다. 유저목록 pagination의 진입 단계입니다. UI 디자인상 트윗에 좋아요를 한 유저가 한 명도 없을 경우 '좋아요 유저목록' 조회링크 자체가 생성되지 않고, 따라서 비어있는 유저 목록을 조회할 일도 없기에 Optional을 사용하지 않습니다.
     * @param tweetId 좋아요의 타겟 트윗 id
     * @return 특정 트윗에 좋아요를 표시한 유저 정보 목록
     */
    @Override
    public List<User> findUsersByTweetIdOrderByCreatedAtDesc(String tweetId) {
        return likeTweetMapper.findUsersLikedTweet(tweetId, USER_LOAD_LIMIT);
    }

    /**
     * 좋아요 표시 유저목록 pagination 단계 중 이전에 조회한 목록에 이어 다음 유저들의 정보를 한계설정값의 수 만큼 추가로 조회합니다.
     * @param tweetId 좋아요의 타겟 트윗 id
     * @param userId 이전 pagination 에서 불러온 목록 중 가장 마지막에 위치한 유저의 id
     * @return
     */
    @Override
    public List<User> findUsersByTweetIdAndUserIdOrderByCreatedAtDesc(String tweetId, String userId) {
        return likeTweetMapper.findMoreUsersLikedTweet(tweetId, userId, USER_LOAD_LIMIT);
    }

    /**
     * (내부용 메서드)특정 트윗을 조회합니다.
     */
    public Optional<LikeTweet> findLikeTweetByTweetIdAndUserId(String tweetId, String userId) {
        return likeTweetMapper.findLikeTweet(tweetId, userId);
    }
}
