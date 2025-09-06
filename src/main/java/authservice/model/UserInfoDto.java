package authservice.model;

import authservice.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInfoDto
{

    private String firstName; // first_name

    private String lastName; //last_name

    private Long phoneNumber;

    private String email; // email

    private String profilePic; // profile_pic


    private String username; // user_id

    private String password; // user_id

    private Set<UserRole> roles = new HashSet<>();
}
