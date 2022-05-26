package ge.edu.freeuni.petcarebackend.api.mapper;

import ge.edu.freeuni.petcarebackend.api.dtos.PetServiceDto;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PetServiceMapper {

    PetServiceDto petServiceDto(PetServiceEntity petServiceEntity);

    PetServiceEntity petServiceEntity(PetServiceDto petServiceDto);
}
