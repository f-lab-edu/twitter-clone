package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    
    Optional<User> findById(Long id);

    Optional<User> findByUsernameAndPasswordHash(String username, String passwordHash);

    Optional<User> findByEmailAndPasswordHash(String email, String passwordHash);

    void deleteById(Long id);
}
