package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceType;

import java.math.BigDecimal;

public interface PetServiceSearchRepository {

    SearchResultDTO<PetServiceDTO> search(
            int page, int size, boolean asc, String search,
            PetServiceType petServiceType, String breed, City city,
            BigDecimal longitude, BigDecimal latitude);
}
