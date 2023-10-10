package clone.twitter.event.dto;

import clone.twitter.domain.Tweet;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanOutMessageDto {

    private Tweet tweet;

    private List<String> followerIds;
}
