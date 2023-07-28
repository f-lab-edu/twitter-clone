package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryV1 implements UserRepository{

    private final UserMapper userMapper;

    @Override
    public void save(User user) {
        userMapper.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean isExistingUsername(String username) {
        return userMapper.isExistingUsername(username);
    }

    @Override
    public boolean isExistingEmail(String email) {
        return userMapper.isExistingEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public int deleteById(String id) {
        return userMapper.deleteById(id);
    }
}
