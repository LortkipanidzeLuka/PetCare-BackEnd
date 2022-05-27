package ge.edu.freeuni.petcarebackend.api.controller;

import ge.edu.freeuni.petcarebackend.api.dtos.*;
import ge.edu.freeuni.petcarebackend.api.mapper.AdvertisementMapper;
import ge.edu.freeuni.petcarebackend.api.mapper.PetServiceMapper;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.PetServiceService;
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
@RequestMapping("api/pet-service")
public class PetServiceController {

    @Autowired
    private PetServiceService petServiceService;

    @Autowired
    private PetServiceMapper petServiceMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @GetMapping("{id}")
    public ResponseEntity<PetServiceDto> getDonationById(@PathVariable long id) {
        PetServiceEntity petServiceEntity = petServiceService.getPetServiceById(id);
        return ResponseEntity.ok(petServiceMapper.petServiceDto(petServiceEntity));
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageDTO> getImagesById(@PathVariable Long id) {
        return advertisementMapper.advertisementImageDtoList(petServiceService.lookupImages(id));
    }

    @GetMapping
    public SearchResultDTO<AdvertisementDTO> search(
            @RequestParam("page") @Min(1) int page, @RequestParam("size") @Min(5) int size,
            @RequestParam(name = "orderBy") @Pattern(regexp = "^[a-zA-Z0-9]{1,50}$") Optional<String> orderBy,
            @RequestParam(name = "asc", required = false) boolean ascending,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "petServiceType") Optional<PetServiceTypeDto> petServiceType,
            @RequestParam(name = "color") Optional<Color> color,
            @RequestParam(name = "applicableSex") Optional<SexDto> applicableSex,
            @RequestParam(name = "ageFrom") Optional<Integer> ageFrom,
            @RequestParam(name = "ageUntil") Optional<Integer> ageUntil,
            @RequestParam(name = "breed") Optional<String> breed,
            @RequestParam(name = "city") Optional<City> city
    ) {
        return petServiceService.search(
                page, size, orderBy.orElse(null), ascending, search.orElse(""),
                petServiceType.orElse(null), color.orElse(null), applicableSex.orElse(null),
                ageFrom.orElse(null), ageUntil.orElse(null), breed.orElse(null), city.orElse(null)
        );
    }

    @PostMapping
    public ResponseEntity<PetServiceDto> createPetServiceAdvertisement(
            HttpServletRequest request,
            @Valid @RequestBody PetServiceDto petService
    ) {
        PetServiceEntity petServiceEntity = petServiceMapper.petServiceEntity(petService);
        Long createdId = petServiceService.createAdvertisement(petServiceEntity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping()
    public void updatePetServiceAdvertisement(
            @Valid @RequestBody PetServiceDto petService
    ) {
        PetServiceEntity petServiceEntity = petServiceMapper.petServiceEntity(petService);
        petServiceService.updateAdvertisement(petServiceEntity);
    }

    @DeleteMapping("{id}")
    public void deletePetServiceAdvertisement(
            @PathVariable Long id
    ) {
        petServiceService.deleteAdvertisement(id);
    }
}
