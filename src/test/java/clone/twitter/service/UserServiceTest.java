package clone.twitter.service;

import clone.twitter.dto.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Rollback
    @DisplayName("기입된 정보가 DB에 저장되고 회원가입이 완료됨.")
    public void testSignUpSuccess() {
        // given
        User user = new User("testid", "testusername", "testemail", "testpasswordhash", "testprofilename", LocalDate.now(), LocalDateTime.now(), LocalDateTime.now());

        // when
        userService.signUp(user);

        // then
        Optional<User> savedUser = userService.findUserByIdAndPassword(user.getId(), user.getPasswordHash());
        Assertions.assertThat(user.getId()).isEqualTo(savedUser.get().getId());
        Assertions.assertThat(user.getUsername()).isEqualTo(savedUser.get().getUsername());
        Assertions.assertThat(user.getEmail()).isEqualTo(savedUser.get().getEmail());
        Assertions.assertThat(user.getPasswordHash()).isEqualTo(savedUser.get().getPasswordHash());
        Assertions.assertThat(user.getProfileName()).isEqualTo(savedUser.get().getProfileName());
        Assertions.assertThat(user.getBirthdate()).isEqualTo(savedUser.get().getBirthdate());
    }

    @Test
    @Rollback
    @DisplayName("DB에 중복되는 정보가 있어서 회원가입이 되지 않음")
    public void testSignUpFailureByDuplication() {
        // given
        User user1 = new User("testid", "testusername", "testemail", "testpasswordhash", "testprofilename", LocalDate.now(), LocalDateTime.now(), LocalDateTime.now());
        User user2 = new User("testid", "testusername", "testemail", "testpasswordhash", "testprofilename", LocalDate.now(), LocalDateTime.now(), LocalDateTime.now());
        userService.signUp(user1);

        // when, then
        assertThrows(DuplicateKeyException.class, () -> userService.signUp(user2));
    }

    @Test
    @Rollback
    @DisplayName("로그인 성공")
    public void testLoginSuccess() {
        //given
        User user = new User("testid", "testusername", "testemail", "testpasswordhash", "testprofilename", LocalDate.now(), LocalDateTime.now(), LocalDateTime.now());
        userService.signUp(user);

        //when
        Optional<User> loginUser = userService.findUserByIdAndPassword(user.getId(),user.getPasswordHash());

        //then
        Assertions.assertThat(user.getId()).isEqualTo(loginUser.get().getId());
        Assertions.assertThat(user.getUsername()).isEqualTo(loginUser.get().getUsername());
        Assertions.assertThat(user.getEmail()).isEqualTo(loginUser.get().getEmail());
        Assertions.assertThat(user.getPasswordHash()).isEqualTo(loginUser.get().getPasswordHash());
        Assertions.assertThat(user.getProfileName()).isEqualTo(loginUser.get().getProfileName());
        Assertions.assertThat(user.getBirthdate()).isEqualTo(loginUser.get().getBirthdate());
    }

    @Test
    @Rollback
    @DisplayName("로그인 실패")
    public void testLoginFailure() {
        //given
        User user = new User("testid", "testusername", "testemail", "testpasswordhash", "testprofilename", LocalDate.now(), LocalDateTime.now(), LocalDateTime.now());
        userService.signUp(user);

        //when
        Optional<User> loginUser1 = userService.findUserByIdAndPassword("wrongId",user.getPasswordHash());
        Optional<User> loginUser2 = userService.findUserByIdAndPassword(user.getId(),"wrongPassword");

        //then
        Assertions.assertThat(loginUser1).isEqualTo(Optional.empty());
        Assertions.assertThat(loginUser2).isEqualTo(Optional.empty());
    }
}
