package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
