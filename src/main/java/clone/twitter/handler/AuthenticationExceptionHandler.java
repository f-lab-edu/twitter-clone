package clone.twitter.handler;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_BAD_REQUEST;
import static clone.twitter.util.HttpResponseEntities.RESPONSE_UNAUTHORIZED;

import clone.twitter.exception.NoSuchEntityIdException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Void> handle(NoSuchEntityIdException exception) {
        return RESPONSE_BAD_REQUEST;
    }

    @ExceptionHandler
    public ResponseEntity<Void> handle(HttpClientErrorException exception) {
        return RESPONSE_UNAUTHORIZED;
    }
}

/*
 - @ResponseStatus가 메서드로 하여금 반환하게 하는 형식(`ResponseEntity<>(HttpStatus.*))이
 현재 handle() 메서드 구현상 반환값 형식과 동일하기 때문에, @ResponseStatus를 따로 추가하지 않았습니다.

 - 스프링 3.1부터 @ExceptionHandler의 인수로 받는 예외 타입이해당 애노테이션을 적용하는
 메서드 인수의 예외 타입과 같을 경우, @ExceptionHandler의 인수를 생략할 수 있습니다.
 */