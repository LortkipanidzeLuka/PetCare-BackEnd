package ge.edu.freeuni.petcarebackend.api.dtos;

import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {

    private Long id;

    private String header;

    private LocalDate createDate;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private City city;

    private String description;

    private List<String> tags;

    private AdvertisementType advertisementType;

    private AdvertisementImageDTO primaryImage;

    private UserInfoDTO userInfo;

    public AdvertisementDTO(AdvertisementEntity advertisement, boolean needPrimaryImage) {
        this.id = advertisement.getId();
        this.header = advertisement.getHeader();
        this.createDate = advertisement.getCreateDate();
        this.longitude = advertisement.getLongitude();
        this.latitude = advertisement.getLatitude();
        this.city = advertisement.getCity();
        this.description = advertisement.getDescription();
        this.tags = advertisement.getTags();
        this.advertisementType = advertisement.getAdvertisementType();
        this.userInfo = new UserInfoDTO(advertisement.getCreatorUser());
        if (needPrimaryImage) {
            this.primaryImage = advertisement.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).findAny().map(AdvertisementImageDTO::new).orElseThrow(BusinessException::new);
        }
    }

}
