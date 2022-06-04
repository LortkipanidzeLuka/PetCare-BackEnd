package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {AdvertisementMapper.class})
public interface LostFoundMapper {

    LostFoundEntity dtoToEntity(LostFoundDTO dto);

}
