package texas.springboot.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequest {

    @JsonProperty("login")
    private String login;
    @JsonProperty("password")
    private String password;
    @JsonProperty("firstname")
    private String firstName;
    @JsonProperty("lastname")
    private String lastName;

    public UserDTO toDTO() {
        return UserDTO.builder()
                .login(login)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
