package clone.twitter.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.controller.IndexController;
import java.util.Arrays;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

public class ErrorEntityModel extends EntityModel<Errors> {
    public ErrorEntityModel(Errors errors, Link... links) {
        super(errors, Arrays.asList(links));

        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
