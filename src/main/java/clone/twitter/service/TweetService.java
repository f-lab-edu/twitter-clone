package clone.twitter.service;

import clone.twitter.dto.Tweet;
import clone.twitter.dto.User;
import clone.twitter.mapper.TweetMapper;
import clone.twitter.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class TweetService {

    private final TweetMapper tweetMapper;

    TweetService(TweetMapper tweetMapper){
        this.tweetMapper = tweetMapper;
    }

    public void createNewTweet(Tweet newTweet){
        if(newTweet.getText() == null){
            System.out.println("내용을 작성해주세요");
        }
        tweetMapper.insertNewTweet(newTweet);
    }

    public void deleteTweet(String id){
        tweetMapper.deleteTweet(id);
    }

    public Tweet findTweetByTweetId(String id){
        Tweet findTweet = tweetMapper.findTweetByTweetId(id);
        return findTweet;
    }

    public List<Tweet> findTweetsByUserId(String id) {
        List<Tweet> findTweetList = tweetMapper.findTweetsByUserId(id);
        return findTweetList;
    }
}
