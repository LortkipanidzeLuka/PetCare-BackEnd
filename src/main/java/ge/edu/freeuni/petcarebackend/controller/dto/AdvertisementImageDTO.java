package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdvertisementImageDTO {

    private String title;

    private String content;

    private Boolean isPrimary;

    public AdvertisementImageDTO(AdvertisementImageEntity advertisementImage) {
        this.title = advertisementImage.getTitle();
        this.content = advertisementImage.getContent();
        this.isPrimary = advertisementImage.getIsPrimary();
    }
}
