package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    public DonationDTO(DonationEntity donationEntity, boolean needPrimaryImage) {
        super(donationEntity, needPrimaryImage);
        this.applicablePetList = donationEntity.getApplicablePetList();
        this.donationAdvertisementType = donationEntity.getDonationAdvertisementType();
    }
}
