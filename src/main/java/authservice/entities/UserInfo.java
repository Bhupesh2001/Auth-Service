package authservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserInfo {

    /**
     * Primary key for the UserInfo entity.
     * This is a custom ID (e.g., UUID or assigned manually).
     */
    @Id
    @Column(name = "user_id")
    private String userId;

    /**
     * Unique username for the user.
     */
    private String username;

    /**
     * Encrypted password for authentication.
     */
    private String password;

    /**
     * Represents the roles associated with the user.
     * <p>
     * A user can have multiple roles, and each role can be assigned to multiple users.
     * This is implemented using a join table named 'users_roles'.
     * <p>
     * FetchType.EAGER ensures roles are loaded immediately with the user.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles = new HashSet<>();
}
