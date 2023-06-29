package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;

public interface UserRepository {
    int save(User user);
    
    Optional<User> findById(String id);

    Optional<User> findByUsernameAndPasswordHash(String username, String passwordHash);

    Optional<User> findByEmailAndPasswordHash(String email, String passwordHash);

    int deleteById(String id);
}
