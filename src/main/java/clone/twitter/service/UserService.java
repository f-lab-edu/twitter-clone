package clone.twitter.service;

import clone.twitter.dto.request.UserSignInRequestDto;
import clone.twitter.dto.request.UserSignUpRequestDto;
import clone.twitter.dto.response.UserResponseDto;
import java.util.Optional;

public interface UserService {

    void signUp(UserSignUpRequestDto userSignUpRequestDto);

    boolean isDuplicateUsername(String username);

    boolean isDuplicateEmail(String email);

    Optional<UserResponseDto> signIn(UserSignInRequestDto userSignInRequestDto);

    void signOut();

    boolean deleteUserAccount(String userId, String password);

    Optional<UserResponseDto> getUserProfile(String userId);
}
