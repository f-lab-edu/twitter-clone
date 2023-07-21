package clone.twitter.exception;

public class NoSuchUserIdException extends RuntimeException {

    public NoSuchUserIdException(String message) {
        super(message);
    }
}
