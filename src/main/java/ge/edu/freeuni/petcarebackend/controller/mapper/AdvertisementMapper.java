package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AdvertisementMapper {

    AdvertisementEntity advertisementEntity(AdvertisementDTO advertisementDTO);

    AdvertisementDTO advertisementDTO(AdvertisementEntity advertisementEntity);

    AdvertisementImageEntity advertisementImageEntity(AdvertisementImageDTO advertisementImageDTO);

    AdvertisementImageDTO advertisementImageDto(AdvertisementImageEntity advertisementImageEntity);

    List<AdvertisementImageDTO> advertisementImageDtoList(List<AdvertisementImageEntity> advertisementImageEntityList);
}
