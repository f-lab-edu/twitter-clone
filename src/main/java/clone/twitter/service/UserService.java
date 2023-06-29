package clone.twitter.service;

import clone.twitter.domain.User;
import clone.twitter.dto.request.UserSigninRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import clone.twitter.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public Optional<UserResponseDto> signUp(User user) {
        int affectedRows = userRepository.save(user);

        if (affectedRows > 0) {
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

    /**
     * 1) 이후 구현: Spring Security - DB에 저장된 encode된 password를 user객체에 담아온 후 spring security의 PasswordEncoder.matches()로 대조후 일치시 user를 반환. 2)이후 구현: 예외 처리 - username과 email 필드의 값이 모두 들어있지 않거나, 들어왔없만 일치하는 usernamme/email이 없거나, 패스워드가 일치하지 않는 경우들.
     */
    public Optional<UserResponseDto> signIn(UserSigninRequestDto userSigninRequestDto) {
        // username과 email 필드 값 모두가 비어있을 경우 빈 Optional 객체 반환
        if (userSigninRequestDto.getUsername() == null && userSigninRequestDto.getEmail() == null) {
            return Optional.empty();
        }

        Optional<User> optionalUser = Optional.empty();

        if (userSigninRequestDto.getUsername() != null) {
            optionalUser = userRepository.findByUsernameAndPasswordHash(
                userSigninRequestDto.getUsername(), userSigninRequestDto.getPassword());
        } else {
            optionalUser = userRepository.findByEmailAndPasswordHash(
                userSigninRequestDto.getEmail(), userSigninRequestDto.getPassword());
        }

        // 일치하는 username/email이 없거나, password가 일치하지 않는 경우 빈 Optional 객체 반환
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        UserResponseDto userResponseDto = UserResponseDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .profileName(user.getProfileName())
            .createdDate(user.getCreatedAt().toLocalDate())
            .build();

        return Optional.of(userResponseDto);
        //
        // spring security 적용시 비밀번호 대조 logic(username/email로 DB 조회 -> 일치하는 유저 있을 시 해당 user 객체정보를 반환받아 비밀번호 대조)
        //if (user != null && passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
        //    return user;
        //}
    }

    public boolean deleteUserAccount(String userId) {
        int rowsAffected = userRepository.deleteById(userId);

        return rowsAffected > 0;
    }

}
