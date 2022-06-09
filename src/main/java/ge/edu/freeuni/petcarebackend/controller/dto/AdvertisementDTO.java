package ge.edu.freeuni.petcarebackend.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String header;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createDate;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @NotNull
    private City city;

    private String description;

    private List<String> tags;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AdvertisementType advertisementType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AdvertisementImageDTO primaryImage;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserInfoDTO userInfo;

    private List<AdvertisementImageDTO> images = new ArrayList<>();

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
