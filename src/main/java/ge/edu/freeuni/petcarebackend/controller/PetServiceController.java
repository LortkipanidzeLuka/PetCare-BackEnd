package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.mapper.AdvertisementMapper;
import ge.edu.freeuni.petcarebackend.controller.mapper.PetServiceMapper;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.PetServiceService;
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
@RequestMapping("api/pet-service") // TODO: rename advertisements/petservice, server.prefix=api ?
public class PetServiceController {

    private final PetServiceService petServiceService;

    private final PetServiceMapper petServiceMapper;

    private final AdvertisementMapper advertisementMapper;

    public PetServiceController(PetServiceService petServiceService, PetServiceMapper petServiceMapper, AdvertisementMapper advertisementMapper) {
        this.petServiceService = petServiceService;
        this.petServiceMapper = petServiceMapper;
        this.advertisementMapper = advertisementMapper;
    }

    @GetMapping("{id}")
    public ResponseEntity<PetServiceDTO> getDonationById(@PathVariable long id) {
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
            @RequestParam(name = "petServiceType") Optional<PetServiceType> petServiceType,
            @RequestParam(name = "color") Optional<Color> color,
            @RequestParam(name = "applicableSex") Optional<Sex> applicableSex,
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
    public ResponseEntity<PetServiceDTO> createPetServiceAdvertisement(
            HttpServletRequest request,
            @Valid @RequestBody PetServiceDTO petService
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
            @Valid @RequestBody PetServiceDTO petService
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