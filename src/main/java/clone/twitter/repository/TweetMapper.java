package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TweetMapper {
    List<Tweet> findInitialTimelinePageTweets(@Param("id") String userid, @Param("limit") int limit);

    List<Tweet> findNextTimelinePageTweets(@Param("id") String userid, @Param("createdAt") LocalDateTime createdAt, @Param("limit") int limit);

    Optional<Tweet> findById(String id);

    void save(Tweet tweet);

    void deleteById(String id);
}
