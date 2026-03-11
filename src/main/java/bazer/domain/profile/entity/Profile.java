package bazer.domain.profile.entity;

import bazer.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

    @Column(length = 8)
    private String postalCode;

    private String adressNumber;

    @Column(length = 150)
    private String state;

    @Column(length = 150)
    private String neighborhood;

    @Column(length = 150)
    private String city;

    @Column(length = 15)
    private String phone;

    @OneToOne
    @JoinColumn(name = "fk_userId")
    private User user;


}
