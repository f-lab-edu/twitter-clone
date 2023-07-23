package clone.twitter.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private final  String userId;

    private final  String username;

    private final  String profileName;

    private final  LocalDate createdDate;
}
