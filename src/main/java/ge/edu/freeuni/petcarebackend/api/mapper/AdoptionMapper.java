package ge.edu.freeuni.petcarebackend.api.mapper;

import ge.edu.freeuni.petcarebackend.api.dtos.AdoptionDto;
import ge.edu.freeuni.petcarebackend.repository.entity.AdoptionEntity;
import org.mapstruct.Mapper;

@Mapper
public interface AdoptionMapper {

    AdoptionEntity adoptionEntity(AdoptionDto adoptionDto);

    AdoptionDto adoptionDto(AdoptionEntity adoptionEntity);
}
