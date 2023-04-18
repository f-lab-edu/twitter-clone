package clone.twitter.v0.dto.response;

import clone.twitter.v0.domain.Member;
import java.sql.Date;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 'password' 를 return 이 필요한가?
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long memberId;
    private String displayMemberId;
    private String email;
    private String memberName;
    private Date birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String password;
}
