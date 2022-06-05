package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.AdoptionDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdoptionEntity;
import org.mapstruct.Mapper;

@Mapper
public interface AdoptionMapper {

    AdoptionEntity adoptionEntity(AdoptionDTO adoptionDto);

    AdoptionDTO adoptionDto(AdoptionEntity adoptionEntity);
}
