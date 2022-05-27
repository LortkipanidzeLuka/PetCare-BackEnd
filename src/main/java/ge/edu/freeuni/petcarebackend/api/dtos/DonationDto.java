package ge.edu.freeuni.petcarebackend.api.dtos;

import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DonationDto extends AdvertisementDTO {

    //TODO
//    @NotNull
//    private List<PetType> applicablePetList;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DonationAdvertisementTypeDto donationAdvertisementType;

    @Enumerated(EnumType.STRING)
    private ColorDto color;

    @Min(0)
    @Column(name = "age_from")
    private Short ageFrom;

    @Min(30)
    @Column(name = "age_until")
    private Short ageUntil;

    @Enumerated(EnumType.STRING)
    private SexDto applicableSex;

    //TODO
//    private List<String> applicableBreedList;

    @AssertTrue
    private boolean isValidAge() {
        return ageFrom == null || ageUntil == null || ageUntil >= ageFrom;
    }
}
