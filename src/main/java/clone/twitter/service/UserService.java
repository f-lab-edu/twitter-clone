package clone.twitter.service;

import clone.twitter.dto.User;
import clone.twitter.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void signUp(User user) throws DuplicateKeyException{
            userMapper.insertUser(user);
    }

    public Optional<User> findUserByIdAndPassword(String id, String passwordHash) {

        Optional<User> user = Optional.ofNullable(userMapper.selectUserByIdAndPassword(id, passwordHash));

        if(!user.isPresent()) {
            return Optional.empty();
        }

        if(passwordHash.equals(user.get().getPasswordHash())){
            return user;
        }else{
            return Optional.empty();
        }
    }
}
