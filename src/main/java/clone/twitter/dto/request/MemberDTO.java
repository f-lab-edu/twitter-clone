package clone.twitter.dto.request;

import java.sql.Date;
import java.time.LocalDateTime;

public class MemberDTO {
    private String username;
    private String password;
    private String email;
    private Date birthdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
