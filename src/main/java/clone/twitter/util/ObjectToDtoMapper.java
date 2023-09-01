package clone.twitter.util;

import clone.twitter.domain.User;
import clone.twitter.dto.response.UserResponseDto;

public class ObjectToDtoMapper {

    public static UserResponseDto convertObjectToUserResponsDto(User user) {
        return UserResponseDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .profileName(user.getProfileName())
            .createdDate(user.getCreatedAt().toLocalDate())
            .build();
    }
}
