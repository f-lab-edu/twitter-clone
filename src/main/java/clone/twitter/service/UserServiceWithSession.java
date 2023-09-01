package clone.twitter.service;

import clone.twitter.domain.User;
import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.exception.NoSuchEntityException;
import clone.twitter.repository.UserRepository;
import clone.twitter.util.auth.SessionStorage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class UserServiceWithSession implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final SessionStorage sessionStorage;

    @Override
    public void signUp(UserSignUpRequestDto userSignUpRequestDto) {
        String encryptedPassword = passwordEncoder.encode(userSignUpRequestDto.getPassword());

        User userWithEncryptedPassword = User.builder()
            .id(UUID.randomUUID().toString())
            .username(userSignUpRequestDto.getUsername())
            .email(userSignUpRequestDto.getEmail())
            .passwordHash(encryptedPassword)
            .profileName(userSignUpRequestDto.getProfileName())
            .birthdate(userSignUpRequestDto.getBirthdate())
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

        userRepository.save(userWithEncryptedPassword);

        sessionStorage.storeSession(userWithEncryptedPassword.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateUsername(String username) {
        return userRepository.isExistingUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateEmail(String email) {
        return userRepository.isExistingEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> signIn(UserSignInRequestDto userSignInRequestDto) {

        Optional<User> optionalUser = userRepository.findByEmail(userSignInRequestDto.getEmail());

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        boolean isValidPassword = passwordEncoder.matches(
            userSignInRequestDto.getPassword(),
            optionalUser.get().getPasswordHash());

        if (!isValidPassword) {
            return Optional.empty();
        }

        sessionStorage.storeSession(optionalUser.get().getId());

        UserResponseDto userResponseDto = UserResponseDto.builder()
            .userId(optionalUser.get().getId())
            .username(optionalUser.get().getUsername())
            .profileName(optionalUser.get().getProfileName())
            .createdDate(optionalUser.get().getCreatedAt().toLocalDate())
            .build();

        return Optional.of(userResponseDto);
    }

    @Override
    public void signOut() {
        sessionStorage.discardSession();
    }

    @Override
    public boolean deleteUserAccount(String userId, String password) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new NoSuchEntityException("해당 사용자 ID가 존재하지 않습니다.");
        }

        if (passwordEncoder.matches(password, optionalUser.get().getPasswordHash())) {
            userRepository.deleteById(userId);

            sessionStorage.discardSession();

            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserProfile(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            UserResponseDto userResponseDto = UserResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .profileName(user.getProfileName())
                .createdDate(user.getCreatedAt().toLocalDate())
                .build();

            return Optional.of(userResponseDto);
        } else {
            return Optional.empty();
        }
    }
}