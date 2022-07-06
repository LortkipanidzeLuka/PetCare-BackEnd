package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AnimalHelpType;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;

import java.math.BigDecimal;

public interface AnimalHelpSearchRepository {
    SearchResultDTO<AnimalHelpDTO> search(
            int page, int size, boolean asc, String search,
            AnimalHelpType type, PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city,
            BigDecimal longitude, BigDecimal latitude
    );
}
