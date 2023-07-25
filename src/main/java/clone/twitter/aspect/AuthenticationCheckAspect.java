package clone.twitter.aspect;

import clone.twitter.service.SignInService;
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

    private final SignInService signInService;

    @Before("@annotation(clone.twitter.annotation.AuthenticationCheck)")
    public void checkSignIn() throws HttpClientErrorException {

        String signedInUserId = signInService.getSignedInUserId();

        if (signedInUserId == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }
}
