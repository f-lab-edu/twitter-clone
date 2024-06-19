package clone.twitter.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class RedisListenerErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable error) {

        log.error("RedisMessageListenerContainer에서 오류 발생, 메시지 전달 불가: {}", error.getMessage());
    }
}
