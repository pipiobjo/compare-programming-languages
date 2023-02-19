package texas.springboot.greeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import texas.springboot.greeting.model.GreetingDTO;
import texas.springboot.user.UserService;
import texas.springboot.user.model.UserDTO;

@Service
public class GreetingService {
    private UserService userService;

    @Autowired
    public GreetingService(UserService userService) {
        this.userService = userService;
    }

    public GreetingDTO greet(String username){
        UserDTO userByLogin = userService.getUserByLogin(username);
        if (userByLogin == null) {
            return null;
        }
        String msg = "Hello " + userByLogin.getFirstName() + " " + userByLogin.getLastName() + "!";
        return GreetingDTO.builder()
                .message(msg)
                .firstName(userByLogin.getFirstName())
                .lastName(userByLogin.getLastName())
                .build();
    }
}
