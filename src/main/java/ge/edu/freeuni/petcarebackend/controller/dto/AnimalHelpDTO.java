package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.AnimalHelpEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.AnimalHelpType;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalHelpDTO extends AdvertisementDTO {

    @NotNull
    private PetType petType;

    private Color color;

    @Min(0)
    private Short ageFrom;

    @Min(30)
    private Short ageUntil;

    @NotNull
    private AnimalHelpType type;

    @NotNull
    private Sex sex;

    private String breed;

    public AnimalHelpDTO(AnimalHelpEntity lostFound, boolean needPrimaryImage) {
        super(lostFound, needPrimaryImage);
        this.petType = lostFound.getPetType();
        this.color = lostFound.getColor();
        this.ageFrom = lostFound.getAgeFrom();
        this.ageUntil = lostFound.getAgeUntil();
        this.type = lostFound.getType();
        this.sex = lostFound.getSex();
        this.breed = lostFound.getBreed();
    }

    @AssertTrue
    private boolean isValidAge() {
        return ageFrom == null || ageUntil == null || ageUntil >= ageFrom;
    }

}
