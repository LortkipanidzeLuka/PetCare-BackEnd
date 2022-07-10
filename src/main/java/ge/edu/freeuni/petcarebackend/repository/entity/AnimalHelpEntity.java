package ge.edu.freeuni.petcarebackend.repository.entity;

import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "animal_help_advertisement")
public class AnimalHelpEntity extends AdvertisementEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private PetType petType;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Min(0)
    @Column(name = "age_from")
    private Short ageFrom;

    @Max(99)
    @Column(name = "age_until")
    private Short ageUntil;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AnimalHelpType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sex sex;

    private String breed;

    @AssertTrue
    private boolean isValidAge() {
        return ageFrom == null || ageUntil == null || ageUntil >= ageFrom;
    }
}
