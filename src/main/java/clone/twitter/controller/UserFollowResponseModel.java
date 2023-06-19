package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.dto.response.UserFollowResponseDto;
import java.util.Arrays;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class UserFollowResponseModel extends EntityModel<UserFollowResponseDto> {
    public UserFollowResponseModel(UserFollowResponseDto userFollowResponseDto, Link... links) {
        super(userFollowResponseDto, Arrays.asList(links));

        add(linkTo(methodOn(UserController.class).getUserProfile(userFollowResponseDto.getUserResponseDto().getUserId())).withRel("target-user-profile-page"));

        add(Link.of("/docs/index.html#user-follow").withRel("profile"));
    }
}
