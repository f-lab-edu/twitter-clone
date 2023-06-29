package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import clone.twitter.dto.response.UserResponseDto;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

@Slf4j
public class UserResponseModel extends EntityModel<UserResponseDto> {
    public UserResponseModel(UserResponseDto userResponseDto, Link... links) {
        super(userResponseDto, Arrays.asList(links));

        add(linkTo(methodOn(UserController.class).getUserProfile(userResponseDto.getUserId())).withRel("user-profile"));

        add(linkTo(methodOn(TweetController.class).getInitialTweets(userResponseDto.getUserId())).withRel("timeline"));
    }
}
