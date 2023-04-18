package clone.twitter.v0.dto.request;

import java.sql.Date;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    private Long memberId;

    @NotBlank
    @Size(min = 4, max = 12)
    @Pattern(regexp = "[a-zA-Z\\d]*${3,12}")
    private String displayMemberId;

    @NotBlank
    @Size(min = 4, max = 32)
    @Pattern(regexp = "[a-z\\d]*${3,32}")
    private String password;

    private String email;
    private String memberName;
    private Date birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotBlank
    private String confirmingPassword;
}
