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
@Table(name = "donation_advertisement")
public class DonationEntity extends AdvertisementEntity {

    @Column(name = "value")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = PetType.class)
    @CollectionTable(name = "pet_types", joinColumns = @JoinColumn(name = "advertisement_id"))
    private List<PetType> applicablePetList;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "donation_advertisement_type")
    private DonationAdvertisementType donationAdvertisementType;
}
