package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.repository.entity.Type;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LostFoundDTO extends AdvertisementDTO {

    private PetType petType;

    private Color color;

    private Short ageFrom;

    private Short ageUntil;

    private Type type;

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
}
