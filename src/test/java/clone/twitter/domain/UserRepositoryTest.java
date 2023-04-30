package clone.twitter.domain;

import static org.assertj.core.api.Assertions.assertThat;

import clone.twitter.repository.UserRepository;
import clone.twitter.repository.UserRepositoryV1;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void afterEach() {

        if (userRepository instanceof UserRepositoryV1) {
            ((UserRepositoryV1) userRepository).clear();
        }
    }

    @Test
    void save() {

        // given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        // when
        User savedUser = userRepository.save(user);

        // then
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isNotNull().isEqualTo(Optional.of(savedUser));
    }

    @Test
    void findById() {
        // given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        User savedUser = userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser).isNotNull().isEqualTo(Optional.of(savedUser));
    }

    @Test
    void findByUsernameAndPasswordHash() {

        // given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        User savedUser = userRepository.save(user);
        log.info("###### savedUser = {}", savedUser.toString());

        // when
        Optional<User> foundUser = userRepository.findByUsernameAndPasswordHash(
            savedUser.getUsername(), savedUser.getPasswordHash());

        // then
        assertThat(foundUser).isEqualTo(Optional.of(savedUser));
    }

    @Test
    void findByEmailAndPasswordHash() {

        // given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        User savedUser = userRepository.save(user);
        log.info("###### savedUser = {}", savedUser.toString());

        // when
        Optional<User> foundUser = userRepository.findByEmailAndPasswordHash(
            savedUser.getEmail(), savedUser.getPasswordHash());

        // then
        assertThat(foundUser).isEqualTo(Optional.of(savedUser));
    }

    @Test
    void deleteById() {

        // given
        User user = new User(
            "haro123",
            "haro@gmail.com",
            "b03b29", "haro",
            LocalDate.of(1999, 9, 9)
        );

        User savedUser = userRepository.save(user);

        // when
        userRepository.deleteById(savedUser.getId());

        // then
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }
}
