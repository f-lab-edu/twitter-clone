package clone.twitter.controller;

import clone.twitter.common.BaseControllerTest;
import clone.twitter.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserControllerTest extends BaseControllerTest {
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("POST /users/new - 회원가입: 정상 응답")
    void signUp() {
    }

    @Test
    @DisplayName("GET /users/{id} - user id로 회원 조회: 정상 응답")
    void getUser() {
    }

    @Test
    @DisplayName("POST /users/signin - username으로 로그인: 정상 응답")
    void signInByUsername() {
    }

    @Test
    @DisplayName("POST /users/signin - email로 로그인: 정상 응답")
    void signInByEmail() {
    }

    @Test
    @DisplayName("DELETE /users/{id} - user id로 계정 삭제: 정상 응답")
    void deleteAccount() {
    }
}
