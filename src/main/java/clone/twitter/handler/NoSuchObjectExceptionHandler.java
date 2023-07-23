package clone.twitter.handler;

import static clone.twitter.util.HttpResponseEntities.RESPONSE_BAD_REQUEST;

import clone.twitter.exception.NoSuchUserIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NoSuchObjectExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchUserIdException.class)
    public ResponseEntity<Void> handle(NoSuchUserIdException ex) {
        return RESPONSE_BAD_REQUEST;
    }
}
