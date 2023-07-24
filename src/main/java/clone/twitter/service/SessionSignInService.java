package clone.twitter.service;

import static clone.twitter.util.SessionKeys.USER_ID;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionSignInService implements SignInService{

    private final HttpSession httpSession;

    @Override
    public void signInUser(String userID) {
        httpSession.setAttribute(USER_ID, userID);
    }

    @Override
    public void signOutUser() {
        httpSession.removeAttribute(USER_ID);
    }

    @Override
    public String getSignedInUserId() {
        return (String) httpSession.getAttribute(USER_ID);
    }
}
