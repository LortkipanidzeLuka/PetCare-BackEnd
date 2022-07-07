package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.mapper.AnimalHelpMapper;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.AnimalHelpService;
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
@RequestMapping("/advertisements/animalhelp")
public class AnimalHelpController {

    private final AnimalHelpService service;

    private final AnimalHelpMapper mapper;

    public AnimalHelpController(AnimalHelpService service, AnimalHelpMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("{id}")
    public AnimalHelpDTO getById(@PathVariable Long id) {
        return service.lookupAdvertisement(id);
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageDTO> getImagesById(@PathVariable Long id) {
        return service.lookupImages(id);
    }

    @GetMapping("/search/{type}")
    public SearchResultDTO<AnimalHelpDTO> search(
            @PathVariable AnimalHelpType type,
            @RequestParam("page") @Min(1) int page, @RequestParam("size") @Min(5) int size,
            @RequestParam(name = "asc", required = false) boolean ascending,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "petType") Optional<PetType> petType,
            @RequestParam(name = "color") Optional<Color> color,
            @RequestParam(name = "sex") Optional<Sex> sex,
            @RequestParam(name = "ageFrom") Optional<Integer> ageFrom, @RequestParam(name = "ageUntil") Optional<Integer> ageUntil,
            @RequestParam(name = "breed") Optional<String> breed,
            @RequestParam(name = "city") Optional<City> city,
            @RequestParam(name = "longitude") Optional<BigDecimal> longitude,
            @RequestParam(name = "latitude") Optional<BigDecimal> latitude
    ) {
        return service.search(
                type, page, size, ascending, search.orElse(""),
                petType.orElse(null), color.orElse(null), sex.orElse(null),
                ageFrom.orElse(null), ageUntil.orElse(null), breed.orElse(null), city.orElse(null),
                longitude.orElse(null), latitude.orElse(null)
        );
    }

    @PostMapping
    public ResponseEntity createAnimalHelpAdvertisement(
            HttpServletRequest request, @Valid @RequestBody AnimalHelpDTO animalHelpDTO
    ) {
        AnimalHelpEntity entity = mapper.animalHelpEntity(animalHelpDTO);
        Long createdId = service.createAdvertisement(entity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public void updateAnimalHelpAdvertisement(
            @PathVariable Long id,
            @Valid @RequestBody AnimalHelpDTO animalHelpDTO
    ) {
        service.updateAdvertisement(id, animalHelpDTO);
    }

    @DeleteMapping("{id}")
    public void deleteAnimalHelpAdvertisement(
            @PathVariable Long id
    ) {
        service.deleteAdvertisement(id);
    }

}
