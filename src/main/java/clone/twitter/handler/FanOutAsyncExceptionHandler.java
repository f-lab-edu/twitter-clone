package clone.twitter.handler;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class FanOutAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FanOutAsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        logger.error("Exception thrown in async method: {}", ex.getMessage());

        logger.info("Method name: {}", method.getName());

        for (Object param : params) {
            logger.info("Parameter value: {}", param);
        }
    }
}
