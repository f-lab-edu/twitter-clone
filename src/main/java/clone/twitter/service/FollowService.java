package clone.twitter.service;

import clone.twitter.dto.response.FollowResponseDto;
import clone.twitter.domain.Follow;
import clone.twitter.repository.FollowRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FollowService {
    @Autowired
    private final FollowRepository followRepository;

    public FollowResponseDto follow(String followerId, String followeeId) {
        followRepository.save(new Follow(followerId, followeeId));

        return FollowResponseDto.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .isFollowing(true)
            .build();
    }

    public FollowResponseDto unfollow(String followerId, String followeeId) {
        followRepository.delete(new Follow(followerId, followeeId));

        return FollowResponseDto.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .isFollowing(false)
            .build();
    }

    public FollowResponseDto getFollow(String followerId, String followeeId) {
        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);

        return FollowResponseDto.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .isFollowing(optionalFollow.isPresent())
            .build();
    }
}
