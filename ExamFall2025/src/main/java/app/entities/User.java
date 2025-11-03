package app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Purpose: To handle security in the API
 */
@Entity
@Table(name = "users")
@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // jeg kunne godt lave et @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id
    // hvis projektet skulle skaleres. Lige nu er der kun en user og en admin.

    @Id
    @Basic(optional = false)
    @Column(name = "user_name", length = 25)
    @JsonProperty("username")
    private String username;

    @Basic(optional = false)
    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_name"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<Role> roles = new HashSet<>();

    public Set<String> getRolesAsStrings() {
        if (roles.isEmpty()) {
            return null;
        }
        Set<String> rolesAsStrings = new HashSet<>();
        roles.forEach((role) -> {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    public User(String username, String userPass) {
        this.username = username;
        this.password = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public User(String username, Set<Role> roleEntityList) {
        this.username = username;
        this.roles = roleEntityList;
    }

    public void addRole(Role role) {
        if (role == null) {
            return;
        }
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(String userRole) {
        roles.stream()
                .filter(role -> role.getRoleName().equals(userRole))
                .findFirst()
                .ifPresent(role -> {
                    roles.remove(role);
                    role.getUsers().remove(this);
                });
    }
}

