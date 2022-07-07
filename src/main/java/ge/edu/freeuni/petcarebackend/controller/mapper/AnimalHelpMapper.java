package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AnimalHelpEntity;
import org.mapstruct.Mapper;

@Mapper
public interface AnimalHelpMapper {

    AnimalHelpEntity animalHelpEntity(AnimalHelpDTO animalHelpDTO);

    AnimalHelpDTO animalHelpDTO(AnimalHelpEntity animalHelpEntity);

}
