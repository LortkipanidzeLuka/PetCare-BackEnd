package ge.edu.freeuni.petcarebackend.api.controller;

import ge.edu.freeuni.petcarebackend.api.dtos.DonationDto;
import ge.edu.freeuni.petcarebackend.api.dtos.PetServiceDto;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
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

    @GetMapping("{id}")
    public ResponseEntity<PetServiceDto> getDonationById(@PathVariable long id) {
        PetServiceEntity petServiceEntity = petServiceService.getPetServiceById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("{id}/images")
    public List<AdvertisementImageEntity> getImagesById(@PathVariable Long id) {
        //   return service.lookupImages(type, id);
        return null;
    }

    @GetMapping
    public SearchResultDTO<PetServiceDto> search(
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
//        return petServiceService.search(
//                type, page, size, orderBy.orElse(null), ascending, search.orElse(""),
//                petType.orElse(null), color.orElse(null), sex.orElse(null),
//                ageFrom.orElse(null), ageUntil.orElse(null), breed.orElse(null), city.orElse(null)
//        );
        return null;
    }

    @PostMapping
    public ResponseEntity<PetServiceDto> createPetServiceAdvertisement(
            HttpServletRequest request,
            @Valid @RequestBody PetServiceDto petService
    ) {
        PetServiceEntity petServiceEntity = new PetServiceEntity();
        Long createdId = petServiceService.createAdvertisement(petServiceEntity);
        try {
            return ResponseEntity.created(new URI(request.getRequestURL().append("/").append(createdId.toString()).toString()))
                    .header("Access-Control-Expose-Headers", "location").build();
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("{id}")
    public void updatePetServiceAdvertisement(
            @Valid @RequestBody PetServiceDto petService
    ) {
        PetServiceEntity petServiceEntity = new PetServiceEntity();
        petServiceService.updateAdvertisement(petServiceEntity);
    }

    @DeleteMapping("{id}")
    public void deletePetServiceAdvertisement(
            @PathVariable Long id
    ) {
        petServiceService.deleteAdvertisement(id);
    }
}
