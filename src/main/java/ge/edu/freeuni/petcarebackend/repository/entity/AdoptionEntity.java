package ge.edu.freeuni.petcarebackend.repository.entity;

import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "adoption_advertisement")
public class AdoptionEntity extends AdvertisementEntity {

    @NotNull
    @Column(name = "pet_type")
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
    private Sex sex;

    private String breed;
}
