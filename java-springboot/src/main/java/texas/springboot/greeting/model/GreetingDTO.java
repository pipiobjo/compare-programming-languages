package texas.springboot.greeting.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GreetingDTO {
    String message;
    String firstName;
    String lastName;

    public GreetingResponse toResponse() {
        return GreetingResponse.builder()
                .msg(message)
                .firstname(firstName)
                .lastname(lastName)
                .build();
    }

}
