package texas.springboot.greeting.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GreetingResponse {
    String msg;
    String firstname;
    String lastname;
}
