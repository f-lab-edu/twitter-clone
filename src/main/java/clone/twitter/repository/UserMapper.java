package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    void save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsernameAndPasswordHash(@Param("username") String username, @Param("passwordHash") String passwordHash);

    Optional<User> findByEmailAndPasswordHash(@Param("email") String email, @Param("passwordHash") String passwordHash);

    void deleteById(Long id);

    void clear();
}
