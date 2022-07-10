package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;

import java.math.BigDecimal;

public interface DonationSearchRepository {
    SearchResultDTO<DonationDTO> search(int page, int size,  String search,
                                        DonationAdvertisementType donationAdvertisementType, City city,
                                        BigDecimal longitude, BigDecimal latitude);
}
