package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;

public interface UserRepository {

    void save(User user);
    
    Optional<User> findById(String id);

    boolean isExistingUsername(String username);

    boolean isExistingEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    int deleteById(String id);
}
