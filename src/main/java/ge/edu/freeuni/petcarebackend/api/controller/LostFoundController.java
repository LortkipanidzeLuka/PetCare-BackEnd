package ge.edu.freeuni.petcarebackend.api.controller;

import ge.edu.freeuni.petcarebackend.api.dtos.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.api.dtos.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.api.dtos.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.repository.entity.Type;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.LostFoundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("advertisements/lostfound/{type}") // TODO: change requests to dtos
public class LostFoundController {

    private final LostFoundService service;

    public LostFoundController(LostFoundService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public LostFoundDTO getById(@PathVariable Type type, @PathVariable Long id) {
        return service.lookupAdvertisement(type, id);
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageEntity> getImagesById(@PathVariable Type type, @PathVariable Long id) {
        return service.lookupImages(type, id);
    }

    @GetMapping
    public SearchResultDTO<AdvertisementDTO> search(
            @PathVariable Type type,
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
        return service.search(
                type, page, size, orderBy.orElse(null), ascending, search.orElse(""),
                petType.orElse(null), color.orElse(null), sex.orElse(null),
                ageFrom.orElse(null), ageUntil.orElse(null), breed.orElse(null), city.orElse(null)
        );
    }

    @PostMapping
    public ResponseEntity createLostFoundAdvertisement(
            HttpServletRequest request,
            @PathVariable Type type, @Valid @RequestBody LostFoundEntity lostFoundEntity
    ) {
        Long createdId = service.createAdvertisement(type, lostFoundEntity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public void updateLostFoundAdvertisement(
            @PathVariable Type type,
            @PathVariable Long id,
            @Valid @RequestBody LostFoundEntity lostFoundEntity
    ) {
        service.updateAdvertisement(type, id, lostFoundEntity);
    }

    @DeleteMapping("{id}")
    public void deleteLostFoundAdvertisement(
            @PathVariable Type type,
            @PathVariable Long id
    ) {
        service.deleteAdvertisement(type, id);
    }

}
