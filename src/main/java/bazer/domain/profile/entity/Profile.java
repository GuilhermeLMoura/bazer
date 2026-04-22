package bazer.domain.profile.entity;

import bazer.domain.address.entity.Address;
import bazer.domain.assessment_profile.entity.AssessmentProfile;
import bazer.domain.order.entity.Order;
import bazer.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"addresses", "assessmentProfiles", "orders"})
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String name;

    @Column(length = 14, unique = true)
    private String document;

    @Column(length = 1000)
    private String photo;

    @Column(length = 15)
    private String phone;

    @OneToOne
    @JoinColumn(name = "fk_user_id")
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentProfile> assessmentProfiles;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Order> orders;
}
