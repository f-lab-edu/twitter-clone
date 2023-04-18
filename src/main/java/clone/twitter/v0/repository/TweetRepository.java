package clone.twitter.v0.repository;

import java.util.List;
import java.util.Optional;

import clone.twitter.v0.domain.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Optional<Tweet> findById(Long id);
    List<Tweet> findAllByOrderByCreatedAtDesc();
    List<Tweet> findAllByMemberIdByOrderByCreatedAtDesc(String memberId);
}
