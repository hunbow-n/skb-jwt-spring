package hunbow.skillboxjwt.web.model;

import hunbow.skillboxjwt.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {

    private String username;

    private String password;

    private String email;

    private Set<RoleType> roles;


}
