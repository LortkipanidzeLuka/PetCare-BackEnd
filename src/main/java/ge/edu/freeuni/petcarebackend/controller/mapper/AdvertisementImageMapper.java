package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import org.mapstruct.Mapper;

@Mapper
public interface AdvertisementImageMapper {

    AdvertisementImageEntity dtoToEntity(AdvertisementImageDTO dto);

}
