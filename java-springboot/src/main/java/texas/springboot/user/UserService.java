package texas.springboot.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import texas.springboot.user.model.CreateUserResponse;
import texas.springboot.user.exceptions.UserAlreadyExists;
import texas.springboot.user.model.UserDAO;
import texas.springboot.user.model.UserDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public CreateUserResponse createUser(UserDTO user) throws UserAlreadyExists {
        if(repo.findUserByLogin(user.getLogin())!= null){
            throw new UserAlreadyExists("User with login " + user.getLogin() + " already exists");
        }

        UserDAO userDAO = repo.createUser(user);
        return CreateUserResponse.builder()
                .id(userDAO.getId())
                .build();
    }

    public UserDTO getUserByLogin(String login) {
        UserDAO userByLogin = repo.findUserByLogin(login);
        if (userByLogin == null) {
            return null;
        }
        return UserDTO.builder()
                .login(userByLogin.getLogin())
                .password(userByLogin.getPassword())
                .firstName(userByLogin.getFirstName())
                .lastName(userByLogin.getLastName())
                .build();
    }
    
    public List<UserDTO> getAllUsers() {
        List<UserDTO> result = new ArrayList<>();
        List<UserDAO> usersDao = repo.getAllUsers();
        for (UserDAO userDAO : usersDao) {
            result.add(UserDTO.builder()
                    .login(userDAO.getLogin())
                    .password(userDAO.getPassword())
                    .firstName(userDAO.getFirstName())
                    .lastName(userDAO.getLastName())
                    .build());
        }
        return result;
    }


}
