package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import clone.twitter.dto.response.LikeTweetResponseDto;
import java.util.Arrays;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class LikeTweetResponseModel extends EntityModel<LikeTweetResponseDto> {
    public LikeTweetResponseModel(LikeTweetResponseDto likeTweetResponseDto, Link... links) {
        super(likeTweetResponseDto, Arrays.asList(links));

        add(linkTo(TweetController.class).slash(likeTweetResponseDto.getTweetId()).withRel("tweet"));

        add(linkTo(TweetController.class).slash("timeline").withRel("timeline"));
    }

    public void addProfileLink(String fragmentIdentifier) {
        this.add(Link.of("/docs/index.html#" + fragmentIdentifier).withRel("profile"));
    }
}
