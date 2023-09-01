package clone.twitter.repository;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LikeTweetRepositoryV1 implements LikeTweetRepository {

    private final LikeTweetMapper likeTweetMapper;

    @Override
    public void save(LikeTweet likeTweet) {
        likeTweetMapper.likeTweet(likeTweet);
    }

    @Override
    public void deleteByTweetIdAndUserId(String tweetId, String userId) {
        likeTweetMapper.unlikeTweet(tweetId, userId);
    }

    @Override
    public List<User> findUsersByTweetIdOrderByCreatedAtDesc(String tweetId) {
        return likeTweetMapper.findUsersLikedTweet(tweetId, USER_LOAD_LIMIT);
    }

    @Override
    public List<User> findUsersByTweetIdAndUserIdOrderByCreatedAtDesc(String tweetId, String userId) {
        return likeTweetMapper.findMoreUsersLikedTweet(tweetId, userId, USER_LOAD_LIMIT);
    }
}
