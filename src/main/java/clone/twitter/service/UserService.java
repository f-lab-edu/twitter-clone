package clone.twitter.service;

import clone.twitter.domain.User;
import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.exception.NoSuchUserIdException;
import clone.twitter.repository.UserRepository;
import clone.twitter.util.PasswordEncryptor;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_COMMITTED)
@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public void signUp(UserSignUpRequestDto userSignUpRequestDto) {
        String encryptedPassword = PasswordEncryptor.encrypt(userSignUpRequestDto.getPassword());

        User userWithEncryptedPassword = User.builder()
            .id(UUID.randomUUID().toString())
            .username(userSignUpRequestDto.getUsername())
            .email(userSignUpRequestDto.getEmail())
            .passwordHash(encryptedPassword)
            .profileName(userSignUpRequestDto.getProfileName())
            .birthdate(userSignUpRequestDto.getBirthdate())
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();

        userRepository.save(userWithEncryptedPassword);
    }

    public boolean isDuplicateUsername(String username) {
        return userRepository.isExistingUsername(username);
    }

    public boolean isDuplicateEmail(String email) {
        return userRepository.isExistingEmail(email);
    }

    public UserResponseDto signIn(UserSignInRequestDto userSignInRequestDto) {

        Optional<User> optionalUser = userRepository.findByEmail(userSignInRequestDto.getEmail());

        if (optionalUser.isEmpty()) {
            return null;
        }

        boolean isValidPassword = PasswordEncryptor.isMatch(
            userSignInRequestDto.getPassword(),
            optionalUser.get().getPasswordHash());

        if (!isValidPassword) {
            return null;
        }

        return UserResponseDto.builder()
            .userId(optionalUser.get().getId())
            .username(optionalUser.get().getUsername())
            .profileName(optionalUser.get().getProfileName())
            .createdDate(optionalUser.get().getCreatedAt().toLocalDate())
            .build();
    }

    public boolean deleteUserAccount(String userId, String password) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new NoSuchUserIdException("해당 사용자 ID가 존재하지 않습니다.");
        }

        if (PasswordEncryptor.isMatch(password, optionalUser.get().getPasswordHash())) {
            userRepository.deleteById(userId);

            return true;
        }

        return false;
    }

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
