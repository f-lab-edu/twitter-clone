package clone.twitter.controller;

import clone.twitter.dto.Tweet;
import clone.twitter.dto.User;
import clone.twitter.service.TweetService;
import clone.twitter.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tweets")
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService){
        this.tweetService = tweetService;
    }

    @PostMapping("/post")
    public void postTweet(@RequestBody Tweet tweet){
        Tweet newTweet = new Tweet(tweet.getText(), tweet.getUserId());
        tweetService.createNewTweet(newTweet);
    }

    @PostMapping("/delete")
    public void deleteTweet(@RequestBody Tweet tweet){
        tweetService.deleteTweet(tweet.getId());
    }

    @GetMapping("/{tweet_id}")
    public Tweet getTweet(@PathVariable("tweet_id") String id) {
        return tweetService.findTweetByTweetId(id);
    }

    @PostMapping("/feed")
    public List<Tweet> getFeed(@RequestBody User user) {
        return tweetService.findTweetsByUserId(user.getId());
    }

}
