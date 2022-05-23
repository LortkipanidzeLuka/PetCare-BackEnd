package ge.edu.freeuni.petcarebackend.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementImageDto {

    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private String content;

    private Boolean isPrimary;

    private AdvertisementEntity advertisement;

}
