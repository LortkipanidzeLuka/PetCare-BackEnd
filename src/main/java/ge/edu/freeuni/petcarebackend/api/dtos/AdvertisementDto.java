package ge.edu.freeuni.petcarebackend.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDto {

    private Long id;

    @NotBlank
    private String header;

    @NotNull
    private UserDto creatorUser;

    private LocalDate createDate;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @NotNull
    private CityDto city;

    private String description;

    private List<String> tags;

    @Size(max = 10)
    private List<AdvertisementImageEntity> images = new ArrayList<>();
}
