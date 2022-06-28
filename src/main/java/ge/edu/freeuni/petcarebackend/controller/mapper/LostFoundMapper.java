package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AnimalHelpEntity;
import org.mapstruct.Mapper;

@Mapper
public interface LostFoundMapper {

    AnimalHelpEntity lostFoundEntity(LostFoundDTO lostFoundDTO);

    LostFoundDTO lostFoundDTO(AnimalHelpEntity animalHelpEntity);
}
