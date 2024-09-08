package hunbow.skillboxjwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = RoleType.class)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private Set<RoleType> roles = new HashSet<>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @Builder.Default
//    private List<Role> roles = new ArrayList<>();

}
