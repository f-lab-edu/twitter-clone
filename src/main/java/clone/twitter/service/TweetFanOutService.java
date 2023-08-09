package clone.twitter.service;

import static clone.twitter.util.FanOutConstant.INT_NEGATIVE_ONE_AS_END_INDEX_OF_RANGE_SEARCH;
import static clone.twitter.util.FanOutConstant.INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH;
import static clone.twitter.util.FanOutConstant.PREFIX_FOR_CELEB_FOLLOWEE_ID_LIST_KEY;
import static clone.twitter.util.LoadLimitConstant.TWEET_LOAD_LIMIT;

import clone.twitter.domain.Tweet;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.repository.FanOutRepository;
import clone.twitter.repository.TweetRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class TweetFanOutService implements TweetService {

    private final TweetRepository tweetRepository;

    private final FanOutRepository fanOutRepository;

    @Override
    public List<Tweet> getInitialTweets(String userId) {

        List<Tweet> tweetsOfCelebFollowees = lookForTweetsOfCelebFollowee(userId);

        Set<Object> tweetsObjectsOfNonCelebFollowees = fanOutRepository.findTweetsObjectsOfNonCelebFollowees(
                userId,
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                TWEET_LOAD_LIMIT);

        return mergeFolloweeTweets(tweetsOfCelebFollowees, tweetsObjectsOfNonCelebFollowees);
    }

    @Override
    public List<Tweet> getMoreTweets(String userId, LocalDateTime createdAtOfTweet) {

        List<Tweet> tweetsOfCelebFollowees = lookForTweetsOfCelebFollowee(userId);

        Set<Object> tweetsObjectsOfNonCelebFollowees = fanOutRepository.findTweetsObjectsOfNonCelebFollowees(
                userId,
                Double.MIN_VALUE,
                createdAtOfTweet.toEpochSecond(ZoneOffset.UTC),
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                TWEET_LOAD_LIMIT);

        return mergeFolloweeTweets(tweetsOfCelebFollowees, tweetsObjectsOfNonCelebFollowees);
    }

    @Override
    public Optional<Tweet> getTweet(String tweetId) {
        return tweetRepository.findById(tweetId);
    }

    @Override
    public Tweet composeTweet(String userId, TweetComposeRequestDto tweetComposeRequestDto) {

        Tweet tweet = Tweet.builder()
                .id(UUID.randomUUID().toString())
                .text(tweetComposeRequestDto.getText())
                .userId(userId)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        // fan-out 실행
        fanOutRepository.operateFanOut(userId, tweet);

        return tweetRepository.save(tweet);
    }

    @Override
    public void deleteTweet(String tweetId) {

        fanOutRepository.operateDeleteFanOut(tweetId);

        tweetRepository.deleteById(tweetId);
    }

    private List<Tweet> lookForTweetsOfCelebFollowee(String userId) {
        String redisKeyForCelebFolloweeIdList = PREFIX_FOR_CELEB_FOLLOWEE_ID_LIST_KEY + userId;

        List<String> celebFolloweeIds = fanOutRepository.findCelebFolloweeIds(
                redisKeyForCelebFolloweeIdList,
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                INT_NEGATIVE_ONE_AS_END_INDEX_OF_RANGE_SEARCH);

        return fanOutRepository.findListOfTweetsByUserIds(celebFolloweeIds, TWEET_LOAD_LIMIT);
    }

    private static List<Tweet> mergeFolloweeTweets(
            List<Tweet> tweetsOfCelebFollowees,
            Set<Object> tweetsObjectsOfNonCelebFollowees) {

        if (tweetsObjectsOfNonCelebFollowees != null) {

            return Stream.concat(
                            tweetsOfCelebFollowees.stream(),
                            tweetsObjectsOfNonCelebFollowees.stream()
                                    .filter(obj -> obj instanceof Tweet)
                                    .map(obj -> (Tweet) obj))
                    .sorted(Comparator.comparing(
                            Tweet::getCreatedAt,
                            Comparator.reverseOrder()))
                    .limit(TWEET_LOAD_LIMIT)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
// 팔로우중인 팔로우중인 '셀럽 user id 목록'비셀럽 user id 목록 Redis에서 조회 -> RDB에서 해당 유저 트윗 조회
// 1. 팔로우중인 '셀럽 user id 목록' 조회용 Redis Key 생성
// 팔로우중인 팔로우중인 '셀럽 user id 목록'비셀럽 user id 목록 Redis에서 조회 -> RDB에서 해당 유저 트윗 조회
// 2. 팔로우중인 '셀럽 user id 목록' Redis에서 조회
// 3. 팔로우중인 '셀럽 최신 tweet 목록' 조회
// 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 조회
// 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 추가 조회
// 팔로우중인 '셀럽 최신 tweet 목록' 조회
// 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 조회
// 팔로우중인 '셀럽 최신 tweet 목록(from RDB)' + '비셀럽 최신 tweet 목록(from Redis fan-out)' 병합 및 반환
// 팔로우중인 '셀럽 최신 tweet 목록(from RDB)' + '비셀럽 최신 tweet 목록(from Redis fan-out)' 병합 및 반환
// 팔로우중인 '셀럽 최신 tweet 목록' 조회
