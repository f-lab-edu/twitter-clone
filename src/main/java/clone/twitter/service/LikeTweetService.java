package clone.twitter.service;

import clone.twitter.domain.LikeTweet;
import clone.twitter.dto.response.LikeTweetResponseDto;
import clone.twitter.repository.LikeTweetRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
