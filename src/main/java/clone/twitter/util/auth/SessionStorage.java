package clone.twitter.util.auth;

import static clone.twitter.util.auth.SessionConstant.USER_ID;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionStorage {

    private final HttpSession httpSession;

    public void storeSession(String userId) {
        httpSession.setAttribute(USER_ID, userId);
    }

    public void discardSession() {
        httpSession.removeAttribute(USER_ID);
    }

    public String getSessionUserId() {
        return (String) httpSession.getAttribute(USER_ID);
    }
}
