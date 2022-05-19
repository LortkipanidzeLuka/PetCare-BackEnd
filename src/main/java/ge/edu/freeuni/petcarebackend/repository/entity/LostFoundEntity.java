package ge.edu.freeuni.petcarebackend.repository.entity;

import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Entity
@Table(name = "lost_found_advertisement")
public class LostFoundEntity extends AdvertisementEntity {

    @NotNull
    private PetType petType;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Min(0)
    @Column(name = "age_from")
    private Short ageFrom;

    @Min(30)
    @Column(name = "age_until")
    private Short ageUntil;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sex sex;

    private String breed;

    @AssertTrue
    private boolean isValidAge() {
        return ageFrom == null || ageUntil == null || ageUntil >= ageFrom;
    }

}
