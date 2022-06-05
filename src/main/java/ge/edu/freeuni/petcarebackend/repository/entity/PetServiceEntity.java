package ge.edu.freeuni.petcarebackend.repository.entity;


import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pet_service_advertisement")
public class PetServiceEntity extends AdvertisementEntity {

    @NotNull
    @CollectionTable
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = PetType.class)
    private List<PetType> applicablePetList;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PetServiceType petServiceType;

    @Min(0)
    @Column(name = "age_from")
    private Short ageFrom;

    @Min(30)
    @Column(name = "age_until")
    private Short ageUntil;

    @Enumerated(EnumType.STRING)
    private Sex applicableSex;

    @AssertTrue
    private boolean isValidAge() {
        return ageFrom == null || ageUntil == null || ageUntil >= ageFrom;
    }
}
