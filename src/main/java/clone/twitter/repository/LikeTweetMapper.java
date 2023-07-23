package clone.twitter.repository;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeTweetMapper {

    void likeTweet(LikeTweet likeTweet);

    void unlikeTweet(@Param("tweetId") String tweetId, @Param("userId") String userId);

    List<User> findUsersLikedTweet(@Param("tweetId") String tweetId, @Param("limit") int limit);

    List<User> findMoreUsersLikedTweet(@Param("tweetId") String tweetId, @Param("userId") String userId, @Param("limit") int limit);

    Optional<LikeTweet> findLikeTweet(@Param("tweetId") String tweetId, @Param("userId") String userId);
}
