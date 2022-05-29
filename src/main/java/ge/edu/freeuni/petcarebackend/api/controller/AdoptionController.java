package ge.edu.freeuni.petcarebackend.api.controller;

import ge.edu.freeuni.petcarebackend.api.dtos.*;
import ge.edu.freeuni.petcarebackend.api.mapper.AdoptionMapper;
import ge.edu.freeuni.petcarebackend.api.mapper.AdvertisementMapper;
import ge.edu.freeuni.petcarebackend.api.mapper.DonationMapper;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.AdoptionService;
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
@RequestMapping("api/adoptions")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private AdoptionMapper adoptionMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @GetMapping("{id}")
    public ResponseEntity<AdoptionDto> getDonationById(@PathVariable long id) {
        AdoptionEntity adoptionEntity = adoptionService.lookup(id);
        return ResponseEntity.ok(adoptionMapper.adoptionDto(adoptionEntity));
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageDTO> getImagesById(@PathVariable Long id) {
        return advertisementMapper.advertisementImageDtoList(adoptionService.lookupImages(id));
    }

    @GetMapping
    public SearchResultDTO<AdvertisementDTO> search(
            @RequestParam("page") @Min(1) int page, @RequestParam("size") @Min(5) int size,
            @RequestParam(name = "orderBy") @Pattern(regexp = "^[a-zA-Z0-9]{1,50}$") Optional<String> orderBy,
            @RequestParam(name = "asc", required = false) boolean ascending,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "petType") Optional<PetType> petType,
            @RequestParam(name = "color") Optional<Color> color,
            @RequestParam(name = "sex") Optional<Sex> sex,
            @RequestParam(name = "ageFrom") Optional<Integer> ageFrom, @RequestParam(name = "ageUntil") Optional<Integer> ageUntil,
            @RequestParam(name = "breed") Optional<String> breed,
            @RequestParam(name = "city") Optional<City> city
    ) {
        return adoptionService.search(
                page, size, orderBy.orElse(null), ascending, search.orElse(""),
                petType.orElse(null), color.orElse(null), sex.orElse(null),
                ageFrom.orElse(null), ageUntil.orElse(null), breed.orElse(null), city.orElse(null)
        );
    }

    @PostMapping
    public ResponseEntity<DonationDto> createLostFoundAdvertisement( // todo:DonationDto?
            HttpServletRequest request,
            @Valid @RequestBody AdoptionDto adoption
    ) {
        AdoptionEntity adoptionEntity = adoptionMapper.adoptionEntity(adoption);
        Long createdId = adoptionService.createAdvertisement(adoptionEntity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping()
    public void updateDonation(
            @Valid @RequestBody AdoptionDto adoption) {
        AdoptionEntity adoptionEntity = adoptionMapper.adoptionEntity(adoption);
        adoptionService.updateAdvertisement(adoptionEntity);
    }

    @DeleteMapping("{id}")
    public void deleteAdoption(
            @PathVariable Long id
    ) {
        adoptionService.deleteAdvertisement(id);
    }
}
