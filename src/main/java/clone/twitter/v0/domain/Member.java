package clone.twitter.v0.domain;

import java.sql.Date;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * memberId: 생성 후 값 변경 불가
 * DisplayMemberId: 생성 후 유저가 임의로 값 변경 가능
 * 생성자나 RequestDto에 '@Setter' 사용 안하고 Builder 선택 이유:
 * * service 계층에서 requestDto 의 password 를 encode 후 persist 할 때 encode된 password를 전달해야 하는데, 그럴 시
 * 1. service 계층에서 일일이 getter를 사용해 엔터티 생성자의 파라미터로 전달해야 하는데, 같은 타입의 필드들이 있을 시 순서가 영향을 준다
 * 2. requestDto에 setter 사용할 경우 고려 요소
 * * builder의 안전성 측면
 */
@Builder
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue
    private Long memberId;

    @Column(unique = true, nullable = false)
    private String displayMemberId;

    /**
     * '@JsonIgnore': MemberResponseDto 에서 password 필드 자체를 없앴기 때문에 불필요?
     */
    // @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private Date birthdate;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
