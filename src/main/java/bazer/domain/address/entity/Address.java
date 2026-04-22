package bazer.domain.address.entity;

import bazer.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 8)
    private String postalCode;

    @Column(length = 10)
    private String addressNumber;

    @Column(length = 45)
    private String state;

    @Column(length = 45)
    private String neighborhood;

    @Column(length = 45)
    private String city;

    @ManyToOne
    @JoinColumn(name = "fk_profile_id")
    private Profile profile;
}
