package ge.edu.freeuni.petcarebackend.api.dtos;

import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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