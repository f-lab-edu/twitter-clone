package clone.twitter.resolver;

import clone.twitter.annotation.SignedInUserId;
import clone.twitter.session.SessionStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class SignedInUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionStorage sessionStorage;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SignedInUserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        return sessionStorage.getSessionUserId();
    }
}

/*
 - Session storage로 부터 정보를 받아오는 방식의 장점과 단점(VS 클라이언트 request나 DB에서 받아오는 방식)
   - 장점
     - 성능: DB와의 상호작용을 줄일수 있고, 따라서 DB와의 커넥션 체결, 탐색에 드는 연산 비용, I/O 등에서 오는
     오버헤드를 줄일 수 있다.
     - 보안: Http 요청에서 민감한 데이터를 담지 않아도 되므로, 해당 과정에서 사용자 정보가 노출되어 탈취되거나
     조작되는 위험을 줄일 수 있다.
     - 사용자 친화적: 로그인, 인증 등의 까다로운 절차를 생략하고 더 나은 사용자 경험을 제공할할 수 있다.
   - 단점
     - 메모리 사용: 서버의 메모리를 사용함으로써 동시접속 유저가 많을 시 문제가 될 수 있다.
     - 확장성: 서버의 메모리에 세션을 저장하는 방식일 시, scale-out할 경우 각 서버간 세션 정보 공유의 문제가 생긴다.
     - 일관성 유지: Http 통신은 무상태를 전제로 하지만 session을 사용할 시 상태의 개념을 갖게 되며,
     일관성 있는 상태를 유지하기 위한 복잡도가 증가한다. 예를 들어,
       - session을 수정하는 요청이 실패하여 오류가 발생했고 제대로 처리하지 못할 시
       해당 session은 상태의 일관성을 잃게 되고 이로 인해 문제가 발생할 수 있다.
       - 동시성 문제: 무상태 애플리케이션은 동시 병렬 처리에 문제가 없지만, 상태를 유지하는 애플리케이션의 경우
       동시 요청 상황에서 일관성을 유지하기가 어려울 수 있다.
     - 데이터의 최신성, 정합성: 사용자가 중간에 DB의 정보를 변경할 시 세션 데이터가 이를 반영하고 있지 않을 수 있으며,
     정합성에 문제가 생길 수 있다.
 */
