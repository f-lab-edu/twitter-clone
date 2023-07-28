package clone.twitter.service;

import clone.twitter.domain.Follow;
import clone.twitter.dto.request.UserFollowRequestDto;
import clone.twitter.dto.response.FollowResponseDto;
import clone.twitter.dto.response.UserFollowResponseDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.dto.UserFollowDto;
import clone.twitter.util.ObjectToDtoMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;

    public FollowResponseDto follow(String followerId, String followeeId) {
        Follow follow = Follow.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

        followRepository.save(follow);

        return FollowResponseDto.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .isFollowing(true)
            .build();
    }

    public FollowResponseDto unfollow(String followerId, String followeeId) {
        followRepository.delete(followerId, followeeId);

        return FollowResponseDto.builder()
            .followerId(followerId)
            .followeeId(followeeId)
            .isFollowing(false)
            .build();
    }

    public List<UserFollowResponseDto> getUserFollowList(UserFollowRequestDto userFollowRequestDto) {
        List<UserFollowDto> userFollowDtos = followRepository.findByFollowerIdAndFolloweeIdAndCreatedAtOrderByCreatedAtDesc(userFollowRequestDto.getFollowerId(), userFollowRequestDto.getFolloweeId(), userFollowRequestDto.getCreatedAtOfUserLastOnList());

        if (userFollowDtos.isEmpty()) {
            return Collections.emptyList();
        }

        // Following: profile 페이지 주인의 userId가 followerId가 입력값으로 들어왔을 경우
        if (!userFollowDtos.get(0).getFollow().getFollowerId().isEmpty()) {
            return userFollowDtos.stream()
                .map(userFollowDto -> {
                    UserResponseDto userResponseDto = ObjectToDtoMapper.convertObjectToUserResponsDto(userFollowDto.getUser());

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
                UserResponseDto userResponseDto = ObjectToDtoMapper.convertObjectToUserResponsDto(userFollowDto.getUser());

                FollowResponseDto followResponseDto = getFollow(userFollowDto.getFollow().getFolloweeId(), userFollowDto.getFollow().getFollowerId());

                return UserFollowResponseDto.builder()
                    .userResponseDto(userResponseDto)
                    .follow(userFollowDto.getFollow())
                    .isFollowing(followResponseDto.isFollowing())
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
