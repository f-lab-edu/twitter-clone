package clone.twitter.service;

import static clone.twitter.util.FanOutConstant.INT_NEGATIVE_ONE_AS_END_INDEX_OF_RANGE_SEARCH;
import static clone.twitter.util.FanOutConstant.INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH;
import static clone.twitter.util.FanOutConstant.PREFIX_FOR_CELEB_FOLLOWEE_ID_LIST_KEY;
import static clone.twitter.util.LoadLimitConstant.TWEET_LOAD_LIMIT;

import clone.twitter.domain.Tweet;
import clone.twitter.domain.User;
import clone.twitter.dto.request.TweetComposeRequestDto;
import clone.twitter.exception.NoSuchEntityException;
import clone.twitter.repository.FanOutRepository;
import clone.twitter.repository.FollowRepository;
import clone.twitter.repository.TweetRepository;
import clone.twitter.repository.UserRepository;
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

    private final UserRepository userRepository;

    private final TweetRepository tweetRepository;

    private final FollowRepository followRepository;

    private final FanOutRepository fanOutRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Tweet> getInitialTweets(String userId) {

        // 1. 팔로우중인 '셀럽 user id 목록' Redis에서 조회
        List<Tweet> tweetsOfCelebFollowees = lookForTweetsOfCelebFollowees(userId);

        // 2. 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 조회
        Set<Object> tweetsObjectsOfNonCelebFollowees = fanOutRepository.findTweetsObjectsOfNonCelebFollowees(
                userId,
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                TWEET_LOAD_LIMIT);

        // 3. 팔로우중인 '셀럽 최신 tweet 목록(from RDB)' + '비셀럽 최신 tweet 목록(from Redis fan-out)' 병합 및 반환
        return mergeFolloweeTweets(tweetsOfCelebFollowees, tweetsObjectsOfNonCelebFollowees);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tweet> getMoreTweets(String userId, LocalDateTime createdAtOfTweet) {

        // 1. 팔로우중인 '셀럽 user id 목록' Redis에서 조회후 '셀럽유저 최신 tweet 목록' RDB에서 조회
        List<Tweet> tweetsOfCelebFollowees = lookForTweetsOfCelebFollowees(userId);

        // 2. 팔로우중인 '일반유저 최신 tweet 목록(fanned-out to Redis)' 조회
        Set<Object> tweetsObjectsOfNonCelebFollowees = fanOutRepository.findTweetsObjectsOfNonCelebFollowees(
                userId,
                Double.MIN_VALUE,
                createdAtOfTweet.toEpochSecond(ZoneOffset.UTC),
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                TWEET_LOAD_LIMIT);

        // 3. 팔로우중인 '셀럽 최신 tweet 목록(from RDB)' + '비셀럽 최신 tweet 목록(from Redis fan-out)' 병합 및 반환
        return mergeFolloweeTweets(tweetsOfCelebFollowees, tweetsObjectsOfNonCelebFollowees);
    }

    @Override
    @Transactional(readOnly = true)
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

        // RDB에 트윗 저장
        tweetRepository.save(tweet);

        // (본인이 셀럽계정에 해당하지 않는 경우에 한해)Redis에 fan-out 실행
        if (!checkIfCelebrity(userId)) {

            // 본인을 팔로우하는 userId(followeeId) 목록 조회
            List<String> followerIds = followRepository.findFollowerIdsByFolloweeId(userId);

            fanOutRepository.operateFanOut(followerIds, tweet);
        }

        return tweet;
    }

    @Override
    public void deleteTweet(String tweetId) {

        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);

        if (optionalTweet.isEmpty()) {
            throw new NoSuchEntityException("해당 트윗이 존재하지 않습니다.");
        }

        // RDB에서 트윗 삭제
        tweetRepository.deleteById(tweetId);

        Tweet tweet = optionalTweet.get();

        // (본인이 셀럽계정에 해당하지 않는 경우에 한해)Redis에서 삭제 fan-out 실행
        if (!checkIfCelebrity(tweet.getUserId())) {

            // 본인을 팔로우하는 userId(followeeId) 목록 조회
            List<String> followerIds = followRepository.findFollowerIdsByFolloweeId(
                tweet.getUserId());

            fanOutRepository.operateDeleteFanOut(followerIds, tweet);
        }
    }

    private List<Tweet> lookForTweetsOfCelebFollowees(String userId) {
        // 1. 팔로우중인 '셀럽 user id 목록' 조회용 Redis Key 생성
        String redisKeyForCelebFolloweeIdList = PREFIX_FOR_CELEB_FOLLOWEE_ID_LIST_KEY + userId;

        // 2. 팔로우중인 '셀럽 user id 목록' Redis에서 조회
        List<String> celebFolloweeIds = fanOutRepository.findCelebFolloweeIds(
                redisKeyForCelebFolloweeIdList,
                INT_ZERO_AS_START_INDEX_OF_RANGE_SEARCH,
                INT_NEGATIVE_ONE_AS_END_INDEX_OF_RANGE_SEARCH);

        // 3. 팔로우중인 '셀럽 최신 tweet 목록' RDB에서 조회
        return fanOutRepository.findListOfTweetsByUserIds(celebFolloweeIds, TWEET_LOAD_LIMIT);
    }

    private static List<Tweet> mergeFolloweeTweets(
            List<Tweet> tweetsOfCelebFollowees,
            Set<Object> tweetsObjectsOfNonCelebFollowees) {

        if (tweetsObjectsOfNonCelebFollowees != null) {

            // 팔로우중인 '셀럽 최신 tweet 목록(from RDB)' + '비셀럽 최신 tweet 목록(from Redis fan-out)' 병합
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

    private boolean checkIfCelebrity(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new NoSuchEntityException("해당 유저가 존재하지 않습니다.");
        }

        return optionalUser.get().isCelebrity();
    }
}
