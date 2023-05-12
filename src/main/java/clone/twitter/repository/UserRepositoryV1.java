package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * for user APIs
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepositoryV1 implements UserRepository{
    private final UserMapper userMapper;

    /**
     * for signup api request
     * @param user username input from user
     * @return all information of a specific user account
     */
    @Override
    public User save(User user) {
        userMapper.save(user);
        return user;
    }

    /**
     * this method is primarily for internal operations
     * @param id primary key of specific user account
     * @return all information of a specific user account, or null if not present
     */
    @Override
    public Optional<User> findById(String id) {
        return userMapper.findById(id);
    }

    /**
     * for login api request when username is chosen for account verification
     * @param username username input from user
     * @param passwordHash password input from user after encoded
     * @return all information of a specific user account, or null if not present
     * @see #findByEmailAndPasswordHash
     */
    @Override
    public Optional<User> findByUsernameAndPasswordHash(String username, String passwordHash) {
        return userMapper.findByUsernameAndPasswordHash(username, passwordHash);
    }

    /**
     * for login api request when email is chosen for account verification
     * @param email email input from user
     * @param passwordHash password input from user after encoded
     * @return all information of a specific user account, or null if not present
     * @see #findByUsernameAndPasswordHash
     */
    @Override
    public Optional<User> findByEmailAndPasswordHash(String email, String passwordHash) {
        return userMapper.findByEmailAndPasswordHash(email, passwordHash);
    }

    /**
     * for user account delete request
     * @param id id of the user
     */
    @Override
    public void deleteById(String id) {
        userMapper.deleteById(id);
    }
}
