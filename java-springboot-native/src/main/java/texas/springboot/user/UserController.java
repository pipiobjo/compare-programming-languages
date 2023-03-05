package texas.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import texas.springboot.user.exceptions.UserAlreadyExists;
import texas.springboot.user.model.CreateUserRequest;
import texas.springboot.user.model.CreateUserResponse;
import texas.springboot.user.model.UserDTO;
import texas.springboot.user.model.UserResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService service) {
        this.userService = service;
    }

    @PostMapping()
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUser){
        CreateUserResponse result = null;
        try {
            result = userService.createUser(createUser.toDTO());
        } catch (UserAlreadyExists e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);

    }
    @GetMapping("/{login}")
    public UserResponse getUsersByLogin(@PathVariable String login){

            UserDTO userByLogin = userService.getUserByLogin(login);
            return userByLogin.toResponse();
    }

    @GetMapping()
    public List<UserResponse> getUsers(){

        List<UserResponse> result = new ArrayList<>();

            List<UserDTO> userDTOS = userService.getAllUsers();
            for (UserDTO userDTO : userDTOS) {
                result.add(userDTO.toResponse());
            }
        return result;
    }
}
