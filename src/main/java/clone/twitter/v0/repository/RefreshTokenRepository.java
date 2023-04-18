package clone.twitter.v0.repository;


import clone.twitter.v0.domain.Member;
import clone.twitter.v0.domain.RefreshToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByMember(Member member);
}
