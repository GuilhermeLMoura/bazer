package bazer.domain.user.entity;

import bazer.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private EnumRole role;

    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @OneToOne(mappedBy = "user")
    private Profile profile;

}
