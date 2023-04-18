package clone.twitter.v0.repository;

import clone.twitter.v0.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDisplayMemberId(String displayMemberId);
    Optional<Member> findByEmail(String email);
}
