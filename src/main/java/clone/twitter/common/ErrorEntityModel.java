package clone.twitter.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.controller.IndexController;
import clone.twitter.dto.request.TweetComposeRequestDto;
import java.util.Arrays;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

/**
 * not tested(현재 비즈니스 로직상 해당사항 없음). 이후 적용 예정.
 * @see clone.twitter.controller.TweetController#composeTweet(TweetComposeRequestDto, Errors)
 * @see clone.twitter.common.ErrorSerializer
 * @see clone.twitter.controller.TweetValidator
 */
public class ErrorEntityModel extends EntityModel<Errors> {
    public ErrorEntityModel(Errors errors, Link... links) {
        super(errors, Arrays.asList(links));

        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
