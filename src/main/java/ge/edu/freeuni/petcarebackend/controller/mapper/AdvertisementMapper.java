package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper
public interface AdvertisementMapper {

    AdvertisementEntity dtoToEntity(AdvertisementDTO advertisementDTO);

}
