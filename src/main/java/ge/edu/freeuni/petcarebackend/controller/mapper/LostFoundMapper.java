package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import org.mapstruct.Mapper;

@Mapper
public interface LostFoundMapper {

    LostFoundEntity lostFoundEntity(LostFoundDTO lostFoundDTO);

    LostFoundDTO lostFoundDTO(LostFoundEntity lostFoundEntity);
}
