package clone.twitter.v0.service;

import clone.twitter.v0.domain.Member;
import clone.twitter.v0.domain.UserDetailsImpl;
import clone.twitter.v0.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String displayMemberId) throws UsernameNotFoundException {
    Optional<Member> member = memberRepository.findByDisplayMemberId(displayMemberId);
    return member
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException("nickname not found"));
  }
}
