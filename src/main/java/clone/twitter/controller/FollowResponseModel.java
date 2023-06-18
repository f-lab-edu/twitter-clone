package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.dto.response.FollowResponseDto;
import java.util.Arrays;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class FollowResponseModel extends EntityModel<FollowResponseDto> {
    public FollowResponseModel(FollowResponseDto followResponseDto, Link... links) {
        super(followResponseDto, Arrays.asList(links));

        add(linkTo(methodOn(UserController.class).getUserProfile(followResponseDto.getFollowerId())).withRel("user-profile-page"));
    }

    public void addProfileLink(String fragmentIdentifier) {
        this.add(Link.of("/docs/index.html#" + fragmentIdentifier).withRel("profile"));
    }
}
