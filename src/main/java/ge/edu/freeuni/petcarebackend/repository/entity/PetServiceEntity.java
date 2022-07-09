package ge.edu.freeuni.petcarebackend.repository.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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

    @Column(name = "value")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = PetType.class)
    @CollectionTable(name = "pet_types", joinColumns = @JoinColumn(name = "advertisement_id"))
    private List<PetType> applicablePetList;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "pet_service_type")
    private PetServiceType petServiceType;
}
