package clone.twitter.aspect;

import clone.twitter.util.auth.SessionStorage;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationCheckAspect {

    private final SessionStorage sessionStorage;

    @Before("@annotation(clone.twitter.annotation.AuthenticationCheck)")
    public void checkSignIn() throws HttpClientErrorException {

        String signedInUserId = sessionStorage.getSessionUserId();

        if (signedInUserId == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }
}

/*
 - 프록시 기반인 Spring AOP는 메서드 실행 지점만 포인트컷으로 선별 가능
 - 포인트컷을 `@Before`로 설정해 메서드 실행 이전에 해당 어드바이스를 실행하도록 하였습니다.
 - `@annotation` 지시자로 주어진 타입의 애노테이션을 갖고 있는 메서드 실행지점을 조인포인트로 매칭하도록 하였습니다.
 */
