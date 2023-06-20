package clone.twitter.service;

import clone.twitter.dto.request.UserFollowRequestDto;
import clone.twitter.dto.response.FollowResponseDto;
import clone.twitter.domain.Follow;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.dto.response.UserFollowResponseDto;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.dto.UserFollowDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
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

    public List<UserFollowResponseDto> getUserFollowList(UserFollowRequestDto followUsersRequestDto) {
        List<UserFollowDto> userFollowDtos = followRepository.findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(followUsersRequestDto.getFollowerId(), followUsersRequestDto.getFolloweeId(), followUsersRequestDto.getCreatedAtOfUserLastOnList());

        if (userFollowDtos.isEmpty()) {
            return Collections.emptyList();
        }

        // Following: profile 페이지 주인의 userId가 followerId가 입력값으로 들어왔을 경우
        if (!userFollowDtos.get(0).getFollow().getFollowerId().isEmpty()) {
            return userFollowDtos.stream()
                .map(userFollowDto -> {
                    UserResponseDto userResponseDto = UserResponseDto.builder()
                        .userId(userFollowDto.getUser().getId())
                        .username(userFollowDto.getUser().getUsername())
                        .profileName(userFollowDto.getUser().getProfileName())
                        .createdDate(userFollowDto.getUser().getCreatedAt().toLocalDate())
                        .build();

                    return UserFollowResponseDto.builder()
                        .userResponseDto(userResponseDto)
                        .follow(userFollowDto.getFollow())
                        .isFollowing(true)
                        .build();
                })
                .collect(Collectors.toList());
        }

        // Followers: profile 페이지 주인의 userId가 followeeId가 입력값으로 들어왔을 경우
        return userFollowDtos.stream()
            .map(userFollowDto -> {
                UserResponseDto userResponseDto = UserResponseDto.builder()
                    .userId(userFollowDto.getUser().getId())
                    .username(userFollowDto.getUser().getUsername())
                    .profileName(userFollowDto.getUser().getProfileName())
                    .createdDate(userFollowDto.getUser().getCreatedAt().toLocalDate())
                    .build();

                FollowResponseDto followResponseDto = getFollow(userFollowDto.getFollow().getFolloweeId(), userFollowDto.getFollow().getFollowerId());

                return UserFollowResponseDto.builder()
                    .userResponseDto(userResponseDto)
                    .follow(userFollowDto.getFollow()) // Follow와 FollowResponseDto 객체의 followerId, followeeId 중복. 이후 Dto에서 중복부분 제거 및 통합하도록 리팩토링
                    .isFollowing(followResponseDto.isFollowing()) // Follow와 FollowResponseDto 객체의 followerId, followeeId 중복. 이후 Dto에서 중복부분 제거 및 통합하도록 리팩토링
                    .build();
            })
            .collect(Collectors.toList());
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
