package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @GetMapping("/tweets")
    public RepresentationModel index() {
        var indexModel = new RepresentationModel<>();
        indexModel.add(linkTo(TweetController.class).slash("timeline").withRel("timeline-tweets"));
        return indexModel;
    }
}
