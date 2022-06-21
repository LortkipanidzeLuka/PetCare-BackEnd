package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DonationDTO extends AdvertisementDTO {

    @NotNull
    private List<PetType> applicablePetList;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DonationAdvertisementType donationAdvertisementType;

    @Enumerated(EnumType.STRING)
    private Color color;

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
