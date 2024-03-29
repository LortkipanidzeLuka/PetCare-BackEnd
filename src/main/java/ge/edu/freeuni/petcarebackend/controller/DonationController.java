package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.mapper.AdvertisementMapper;
import ge.edu.freeuni.petcarebackend.controller.mapper.DonationMapper;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import ge.edu.freeuni.petcarebackend.service.DonationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/advertisements/donations")
public class DonationController {

    private final DonationService donationService;

    private final DonationMapper donationMapper;

    private final AdvertisementMapper advertisementMapper;

    public DonationController(DonationService donationService, DonationMapper donationMapper, AdvertisementMapper advertisementMapper) {
        this.donationService = donationService;
        this.donationMapper = donationMapper;
        this.advertisementMapper = advertisementMapper;
    }

    @GetMapping("{id}")
    public ResponseEntity<DonationEntity> getDonationById(@PathVariable long id) {
        DonationEntity donationEntity = donationService.getDonationById(id);
        return ResponseEntity.ok(donationEntity);
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageDTO> getImagesById(@PathVariable Long id) {
        return advertisementMapper.advertisementImageDtoList(donationService.lookupImages(id));
    }

    @GetMapping("/search/{type}")
    public SearchResultDTO<DonationDTO> search(
            @PathVariable DonationAdvertisementType type,
            @RequestParam("page") @Min(1) int page, @RequestParam("size") @Min(5) int size,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "city") Optional<City> city,
            @RequestParam(name = "longitude") Optional<BigDecimal> longitude,
            @RequestParam(name = "latitude") Optional<BigDecimal> latitude) {
        return donationService.search(
                page, size,  search.orElse(""),
                type, city.orElse(null),
                longitude.orElse(null), latitude.orElse(null)
        );
    }

    @PostMapping
    public ResponseEntity<DonationDTO> createLostFoundAdvertisement(
            HttpServletRequest request,
            @Valid @RequestBody DonationDTO donation
    ) {
        DonationEntity donationEntity = donationMapper.donationEntity(donation);
        Long createdId = donationService.createAdvertisement(donationEntity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public void updateDonation(
            @PathVariable Long id,
            @Valid @RequestBody DonationDTO donation) {
        donation.setId(id);
        DonationEntity donationEntity = donationMapper.donationEntity(donation);
        donationService.updateAdvertisement(donationEntity);
    }

    @DeleteMapping("{id}")
    public void deleteDonation(
            @PathVariable Long id
    ) {
        donationService.deleteAdvertisement(id);
    }

    @PutMapping("{id}/refresh")
    public void refreshDonation(
            @PathVariable Long id
    ) {
        donationService.refreshAdvertisement(id);
    }
}
