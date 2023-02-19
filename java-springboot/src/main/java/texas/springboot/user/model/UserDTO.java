package texas.springboot.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String login;
    private String password;
    private String firstName;
    private String lastName;

    public UserResponse toResponse() {
        return UserResponse.builder()
                .login(login)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
