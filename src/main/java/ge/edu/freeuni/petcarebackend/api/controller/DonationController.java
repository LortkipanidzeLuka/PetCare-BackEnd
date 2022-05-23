package ge.edu.freeuni.petcarebackend.api.controller;

import ge.edu.freeuni.petcarebackend.api.dtos.*;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping("{id}")
    public ResponseEntity<DonationDto> getDonationById(@PathVariable long id) {
        DonationEntity donationEntity = donationService.getDonationById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageEntity> getImagesById(@PathVariable Long id) {
           return donationService.lookupImages(id);
    }

    @GetMapping
    public SearchResultDTO<DonationDto> search(
            @PathVariable Type type,
            @RequestParam("page") @Min(1) int page, @RequestParam("size") @Min(5) int size,
            @RequestParam(name = "orderBy") @Pattern(regexp = "^[a-zA-Z0-9]{1,50}$") Optional<String> orderBy,
            @RequestParam(name = "asc", required = false) boolean ascending,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "petType") Optional<PetTypeDto> petType,
            @RequestParam(name = "color") Optional<ColorDto> color,
            @RequestParam(name = "sex") Optional<SexDto> sex,
            @RequestParam(name = "ageFrom") Optional<Integer> ageFrom,
            @RequestParam(name = "ageUntil") Optional<Integer> ageUntil,
            @RequestParam(name = "breed") Optional<String> breed,
            @RequestParam(name = "city") Optional<CityDto> city)
    {
//        return donationService.search(
//                type, page, size, orderBy.orElse(null), ascending, search.orElse(""),
//           //     petType.orElse(null), color.orElse(null), sex.orElse(null),
//                null, null, null,
//                ageFrom.orElse(null), ageUntil.orElse(null), breed.orElse(null),
//                null
//                //city.orElse(null)
//        );
        return null;
    }

    @PostMapping
    public ResponseEntity<DonationDto> createLostFoundAdvertisement(
            HttpServletRequest request,
            @Valid @RequestBody DonationDto donation
    ) {
        DonationEntity donationEntity = new DonationEntity();
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
            @Valid @RequestBody DonationDto donationDto) {
                DonationEntity donation = new DonationEntity();
        donationService.updateAdvertisement(donation);
    }

    @DeleteMapping("{id}")
    public void deleteDonation(
            @PathVariable Long id
    ) {
        donationService.deleteAdvertisement(id);
    }
}
