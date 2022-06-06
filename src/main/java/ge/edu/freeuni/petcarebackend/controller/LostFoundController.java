package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.mapper.LostFoundMapper;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.service.LostFoundService;
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
@RequestMapping("lostfound")
public class LostFoundController {

    private final LostFoundService service;

    private final LostFoundMapper mapper;

    public LostFoundController(LostFoundService service, LostFoundMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("{id}")
    public LostFoundDTO getById(@PathVariable Long id) {
        return service.lookupAdvertisement(id);
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageDTO> getImagesById(@PathVariable Long id) {
        return service.lookupImages(id);
    }

    @GetMapping("{type}")
    public SearchResultDTO<AdvertisementDTO> search(
            @PathVariable LostFoundType type,
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
            HttpServletRequest request, @Valid @RequestBody LostFoundDTO lostFoundDTO
    ) {
        LostFoundEntity entity = mapper.lostFoundEntity(lostFoundDTO);
        Long createdId = service.createAdvertisement(entity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public void updateLostFoundAdvertisement(
            @PathVariable Long id,
            @Valid @RequestBody LostFoundDTO lostFoundDTO
    ) {
        service.updateAdvertisement(id, lostFoundDTO);
    }

    @DeleteMapping("{id}")
    public void deleteLostFoundAdvertisement(
            @PathVariable Long id
    ) {
        service.deleteAdvertisement(id);
    }

}
