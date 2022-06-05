package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PetServiceMapper {

    PetServiceDTO petServiceDto(PetServiceEntity petServiceEntity);

    PetServiceEntity petServiceEntity(PetServiceDTO petServiceDto);
}
