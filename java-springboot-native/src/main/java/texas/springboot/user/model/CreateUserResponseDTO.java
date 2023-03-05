package texas.springboot.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponseDTO {
    private String id;
}
