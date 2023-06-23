package clone.twitter.repository;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import java.util.List;
import java.util.Optional;

public interface LikeTweetRepository {
    /**
     * 특정 트윗에 좋아요를 표시 유저목록을 조회할 시 1회에 불러올 수 있는 유저 정보의 수 한계설정값
     */
    static final int USER_LOAD_LIMIT = 3;

    void save(LikeTweet likeTweet);

    void deleteByTweetIdAndUserId(String tweetId, String userId);

    List<User> findUsersByTweetIdOrderByCreatedAtDesc(String tweetId);

    List<User> findUsersByTweetIdAndUserIdOrderByCreatedAtDesc(String tweetId, String userId);
}
