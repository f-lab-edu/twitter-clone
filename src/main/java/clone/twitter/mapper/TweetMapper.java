package clone.twitter.mapper;

import clone.twitter.dto.Tweet;
import clone.twitter.dto.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TweetMapper {
    void insertNewTweet(Tweet tweet);

    void deleteTweet(String id);

    Tweet findTweetByTweetId(String id);

    List<Tweet> findTweetsByUserId(@Param("id") String id);
}
