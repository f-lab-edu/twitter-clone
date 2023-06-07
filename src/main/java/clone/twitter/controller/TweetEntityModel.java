package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import clone.twitter.domain.Tweet;
import java.util.Arrays;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class TweetEntityModel extends EntityModel<Tweet> {
    public TweetEntityModel(Tweet tweet, Link... links) {
        super(tweet, Arrays.asList(links));
        add(linkTo(TweetController.class).slash(tweet.getId()).withSelfRel());
    }
}
