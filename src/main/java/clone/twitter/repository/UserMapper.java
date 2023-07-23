package clone.twitter.repository;

import clone.twitter.domain.User;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int save(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsernameAndPasswordHash(@Param("username") String username, @Param("passwordHash") String passwordHash);

    Optional<User> findByEmailAndPasswordHash(@Param("email") String email, @Param("passwordHash") String passwordHash);

    int deleteById(String id);
}
