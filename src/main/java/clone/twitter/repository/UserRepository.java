package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    
    Optional<User> findById(String id);

    Optional<User> findByUsernameAndPasswordHash(String username, String passwordHash);

    Optional<User> findByEmailAndPasswordHash(String email, String passwordHash);

    void deleteById(String id);
}
