package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @GetMapping("/")
    public RepresentationModel index() {
        var index = new RepresentationModel<>();
        index.add(linkTo(TweetController.class).withRel("tweets"));
        return index;
    }
}
