package clone.twitter.service;

import clone.twitter.domain.LikeTweet;
import clone.twitter.domain.User;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.repository.LikeTweetRepository;
import clone.twitter.util.ObjectToDtoMapper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class LikeTweetService {
    @Autowired
    private final LikeTweetRepository likeTweetRepository;

    /**
     * exception handling 이후 추가 필요
     */
    public LikeTweetResponseDto likeTweet(String tweetId, String userId) {
        likeTweetRepository.save(new LikeTweet(tweetId, userId));

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

    /**
     * UI 디자인상 트윗에 좋아요를 한 유저가 한 명도 없을 경우 '좋아요 유저목록' 조회링크 자체가 생성되지 않고, 따라서 비어있는 유저 목록을 조회할 일도 없기에 Optional을 사용하지 않습니다.
     */
    public List<UserResponseDto> getUsersLikedTweet(String tweetId) {
        List<User> users = likeTweetRepository.findUsersByTweetIdOrderByCreatedAtDesc(tweetId);

        return users.stream()
            .map(ObjectToDtoMapper::convertObjectToUserResponsDto)
            .collect(Collectors.toList());
    }

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
