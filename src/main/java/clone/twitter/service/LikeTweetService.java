package clone.twitter.service;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.repository.LikeTweetRepository;
import clone.twitter.util.ObjectToDtoMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class LikeTweetService {

    private final LikeTweetRepository likeTweetRepository;

    public LikeTweetResponseDto likeTweet(String tweetId, String userId) {

        LikeTweet likeTweet = LikeTweet.builder()
            .tweetId(tweetId)
            .userId(userId)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

        likeTweetRepository.save(likeTweet);

        return LikeTweetResponseDto.builder()
            .tweetId(tweetId)
            .isLikedByUser(true)
            .build();
    }

    public LikeTweetResponseDto unlikeTweet(String tweetId, String userId) {
        likeTweetRepository.deleteByTweetIdAndUserId(tweetId, userId);

        return LikeTweetResponseDto.builder()
            .tweetId(tweetId)
            .isLikedByUser(false)
            .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersLikedTweet(String tweetId) {
        List<User> users = likeTweetRepository.findUsersByTweetIdOrderByCreatedAtDesc(tweetId);

        return users.stream()
            .map(ObjectToDtoMapper::convertObjectToUserResponsDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getMoreUserLikedTweet(String tweetId, String userIdLastOnList) {
        List<User> users = likeTweetRepository.findUsersByTweetIdAndUserIdOrderByCreatedAtDesc(tweetId, userIdLastOnList);

        if (!users.isEmpty()) {

            return users.stream()
                .map(ObjectToDtoMapper::convertObjectToUserResponsDto)
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
