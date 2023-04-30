package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TweetMapper {

    List<Tweet> findAllByOrderByCreatedAtDesc();

    List<Tweet> findAllByUserIdByOrderByCreatedAtDesc(Long userId);

    Optional<Tweet> findById(Long id);

    void save(Tweet tweet);

    void deleteById(Long id);

    void clear();
}
