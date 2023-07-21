package clone.twitter.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpResponseEntities {

    public static final ResponseEntity<Void> RESPONSE_OK = new ResponseEntity<>(HttpStatus.OK);

    public static final ResponseEntity<Void> RESPONSE_CREATED = new ResponseEntity<>(HttpStatus.CREATED);

    public static final ResponseEntity<Void> RESPONSE_NO_CONTENT = new ResponseEntity<>(HttpStatus.NO_CONTENT);

    public static final ResponseEntity<Void> RESPONSE_NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

    public static final ResponseEntity<Void> RESPONSE_CONFLICT = new ResponseEntity<>(HttpStatus.CONFLICT);

    public static final ResponseEntity<Void> RESPONSE_UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<Void> RESPONSE_BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    public static <T> ResponseEntity<T> noContent() {
        return (ResponseEntity<T>) RESPONSE_NO_CONTENT;
    }

    public static <T> ResponseEntity<T> notFound() {
        return (ResponseEntity<T>) RESPONSE_NOT_FOUND;
    }

    public static <T> ResponseEntity<T> badRequest() {
        return (ResponseEntity<T>) RESPONSE_BAD_REQUEST;
    }
}
