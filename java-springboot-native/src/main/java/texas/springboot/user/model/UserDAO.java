package texas.springboot.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDAO {
    String id;
    String login;
    String password;
    String firstName;
    String lastName;
}
