package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;

public interface AdvertisementSearchRepository {

    SearchResultDTO<AdvertisementDTO> search(
            int page, int size, boolean asc,
            String search, AdvertisementType type, UserEntity creatorUser
    );
}
