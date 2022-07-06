package ge.edu.freeuni.petcarebackend.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceType;
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
public class PetServiceDTO extends AdvertisementDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private List<PetType> applicablePetList;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PetServiceType petServiceType;

    public PetServiceDTO(PetServiceEntity petServiceEntity, boolean needPrimaryImage) {
        super(petServiceEntity, needPrimaryImage);
        this.applicablePetList = petServiceEntity.getApplicablePetList();
        this.petServiceType = petServiceEntity.getPetServiceType();
    }
}

