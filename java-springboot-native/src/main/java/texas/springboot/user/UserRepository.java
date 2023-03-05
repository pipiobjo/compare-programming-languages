package texas.springboot.user;

import org.springframework.stereotype.Service;
import texas.springboot.user.model.UserDAO;
import texas.springboot.user.model.UserDTO;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRepository {
    private ConcurrentHashMap<String, UserDAO> users = new ConcurrentHashMap<>();

    /**
     *
     * @param login
     * @return UserDAO or null if not found
     */
    public UserDAO findUserByLogin(String login) {
        return users.get(login);
    }

    public UserDAO createUser(UserDTO user) {
        UserDAO userDAO = UserDAO.builder()
                .id(UUID.randomUUID().toString())
                .login(user.getLogin())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        users.put(user.getLogin(), userDAO);
        return userDAO;
    }

    public List<UserDAO> getAllUsers() {
        return List.copyOf(users.values());
    }
}
