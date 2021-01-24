package projects.portfoliodemo.domain.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "username")
@ToString(exclude = {"password", "details"})
public class User implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;
    @Builder.Default
    private Boolean active = Boolean.FALSE;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "username",
                    referencedColumnName = "username"),
            indexes = @Index(
                    name = "users_roles_username_idx",
                    columnList = "username"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserDetails details;
}
