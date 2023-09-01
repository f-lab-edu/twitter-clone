package clone.twitter.repository;

import clone.twitter.domain.Tweet;
import java.util.List;
import java.util.Set;

public interface FanOutRepository {

    public List<Tweet> findListOfTweetsByUserIds(List<String> userIds, int loadLimit);

    Set<Object> findTweetsObjectsOfNonCelebFollowees(String userId, int startIndex, int endIndex);

    List<String> findCelebFolloweeIds(String redisKey, int startIndex, int endIndex);

    Set<Object> findTweetsObjectsOfNonCelebFollowees(
            String userId, double minScore, double maxScore, int startIndex, int endIndex);

    void operateFanOut(String userId, Tweet tweet);

    void operateDeleteFanOut(String tweetId);
}
