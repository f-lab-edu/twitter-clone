package clone.twitter.handler;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_BAD_REQUEST;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_UNAUTHORIZED;

import clone.twitter.exception.NoSuchUserIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Void> handle(NoSuchUserIdException exception) {
        return RESPONSE_BAD_REQUEST;
    }

    @ExceptionHandler
    public ResponseEntity<Void> handle(HttpClientErrorException exception) {
        return RESPONSE_UNAUTHORIZED;
    }
}

/*
    각 메서드 반환값을 ResponseEntity<>(HttpStatus.BAD_REQUEST)와 같이
    @ResponseStatus가 반환하는 형식과 동일하게 반환하고 있기 때문에,
    @ResponseStatus의 인수로 HttpStatus 값을 생략하였습니다.

    스프링 3.1부터 @ExceptionHandler의 인수로 받는 예외 타입이 해당 애노테이션을
    적용하는 메서드 인수의 예외 타입과 같을 경우, @ExceptionHandler의 인수를
    생략할 수 있습니다.
 */