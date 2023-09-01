package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    void save(User user);

    Optional<User> findById(String id);

    boolean isExistingUsername(String username);

    boolean isExistingEmail(String email);

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(@Param("email") String email);

    int deleteById(String id);
}
