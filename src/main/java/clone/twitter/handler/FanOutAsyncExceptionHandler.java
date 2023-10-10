package clone.twitter.handler;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

@Slf4j
public class FanOutAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        log.error("fan-out 비동기 메서드 발행 실패: {}", ex.getMessage());

        log.error("실패 fan-out 메서드: {}", method.getName());

        for (Object param : params) {
            log.error("실패 fan-out 메서드 매개변수 정보 상세: {}", param);
        }
    }
}
