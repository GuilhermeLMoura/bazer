package bazer.domain.assessment_profile.entity;

import bazer.domain.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "assessment_profile")
public class AssessmentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer starQuantity;

    @Column(length = 500)
    private String comment;

    /** Perfil VENDEDOR que está sendo avaliado */
    @ManyToOne
    @JoinColumn(name = "fk_profile_id")
    private Profile profile;

    /** Perfil COMPRADOR que realizou a avaliação */
    @ManyToOne
    @JoinColumn(name = "fk_reviewer_id")
    private Profile reviewer;
}
