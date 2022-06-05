package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class LostFoundDTO extends AdvertisementDTO {

    @NotNull
    private PetType petType;

    private Color color;

    @Min(0)
    private Short ageFrom;

    @Min(30)
    private Short ageUntil;

    @NotNull
    private LostFoundType type;

    @NotNull
    private Sex sex;

    private String breed;

    public LostFoundDTO(LostFoundEntity lostFound, boolean needPrimaryImage) {
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
