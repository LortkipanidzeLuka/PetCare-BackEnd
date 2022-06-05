package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data // todo: data?
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionDTO extends AdvertisementDTO {

    @NotNull
    private PetType petType;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Min(0)
    private Short ageFrom;

    @Min(30)
    private Short ageUntil;

    @NotNull
    private Sex sex;

    private String breed;
}
