package clone.twitter.mapper;

import clone.twitter.dto.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User selectUserByIdAndPassword(@Param("id") String id, @Param("passwordHash") String passwordHash);
}
