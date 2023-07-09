package clone.twitter.controller.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tweets")
public class TweetController {
    @PostMapping("/")
    public void addTweet(/*@RequestBody*/) {

    }

    @PostMapping("/like")
    public void addLike() {

    }

    @PostMapping("{tweet_id}/comments")
    public void addComments() {

    }

    @PatchMapping("/{user}/{post}")
    public void increaseView(@PathVariable("user") String user, @PathVariable("post") int postId) {
        //increaseTweetView
    }

    @GetMapping("/{user}/{post}")
    public void getPrevTweetThread(@PathVariable("user") String user, @PathVariable("post") int postId) {
        // getTweetThread
    }

}
