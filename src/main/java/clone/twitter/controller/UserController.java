package clone.twitter.controller;

import clone.twitter.dto.User;
import clone.twitter.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public void signUp(@RequestBody User user){
        userService.signUp(user);
    }

    @PostMapping("/login")
    public void login(@RequestBody User user) {
        String id = user.getId();
        String passwordHash = user.getPasswordHash();

        Optional<User> checkUser = userService.findUserByIdAndPassword(id,passwordHash);
        if(checkUser.isPresent()) {
            System.out.println("로그인 되었습니다.");
        } else {
            System.out.println("계정 정보가 틀렸습니다.");
        }
    }

}
