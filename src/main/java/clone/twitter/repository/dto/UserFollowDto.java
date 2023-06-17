package clone.twitter.repository.dto;

import clone.twitter.domain.Follow;
import clone.twitter.domain.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode // for test purpose only
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowDto {
    private User user;

    private Follow follow;
}
