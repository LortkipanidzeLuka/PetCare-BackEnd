package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.mapper.AdvertisementMapper;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.AnimalHelpRepository;
import ge.edu.freeuni.petcarebackend.repository.AnimalHelpSearchRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import ge.edu.freeuni.petcarebackend.exception.ExceptionKeys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnimalHelpService {

    private final AnimalHelpRepository repository;

    private final AdvertisementImageRepository imageRepository;

    private final SecurityService securityService;

    private final AdvertisementMapper advertisementMapper;

    private final AnimalHelpSearchRepository animalHelpSearchRepository;

    public AnimalHelpService(AnimalHelpRepository repository, AdvertisementImageRepository imageRepository, SecurityService securityService, AdvertisementMapper advertisementMapper, AnimalHelpSearchRepository animalHelpSearchRepository) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.securityService = securityService;
        this.advertisementMapper = advertisementMapper;
        this.animalHelpSearchRepository = animalHelpSearchRepository;
    }


    public AnimalHelpDTO lookupAdvertisement(Long id) {
        return repository.findById(id).map(ad -> new AnimalHelpDTO(ad, false)).orElseThrow(this::getAdvertisementDoesNotExistEx);
    }

    public AnimalHelpEntity lookup(Long id) {
        return repository.findById(id).orElseThrow(this::getAdvertisementDoesNotExistEx);
    }

    public List<AdvertisementImageDTO> lookupImages(Long id) {
        AnimalHelpEntity animalHelpEntity = lookup(id);
        return imageRepository.findByAdvertisement(animalHelpEntity).stream().map(AdvertisementImageDTO::new).collect(Collectors.toList());
    }

    public SearchResultDTO<AnimalHelpDTO> search(
            AnimalHelpType type, int page, int size, boolean asc, String search, // header or description
            PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city,
            BigDecimal longitude, BigDecimal latitude
    ) {
        return animalHelpSearchRepository.search(
                page, size, asc, search,
                type, petType, color, sex,
                ageFrom, ageUntil, breed, city,
                longitude, latitude
        );
    }

    public Long createAdvertisement(AnimalHelpEntity animalHelpEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        animalHelpEntity.setCreateDate(LocalDate.now());
        animalHelpEntity.setCreatorUser(currentUser);
        animalHelpEntity.setAdvertisementType(AdvertisementType.ANIMAL_HELP);
        if (animalHelpEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw getNeedOnePrimaryImage();
        }
        animalHelpEntity.getImages().forEach(i -> i.setAdvertisement(animalHelpEntity));
        return repository.save(animalHelpEntity).getId();
    }

    public void updateAdvertisement(Long id, AnimalHelpDTO animalHelpDTO) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        AnimalHelpEntity animalHelpEntity = repository.findByCreatorUserAndId(currentUser, id).orElseThrow(BusinessException::new);

        animalHelpEntity.setAgeFrom(animalHelpDTO.getAgeFrom());
        animalHelpEntity.setAgeUntil(animalHelpDTO.getAgeUntil());
        animalHelpEntity.setCity(animalHelpDTO.getCity());
        animalHelpEntity.setDescription(animalHelpDTO.getDescription());
        animalHelpEntity.setBreed(animalHelpDTO.getBreed());
        animalHelpEntity.setColor(animalHelpDTO.getColor());
        animalHelpEntity.setSex(animalHelpDTO.getSex());
        animalHelpEntity.setPetType(animalHelpDTO.getPetType());
        animalHelpEntity.setHeader(animalHelpDTO.getHeader());
        animalHelpEntity.setLatitude(animalHelpDTO.getLatitude());
        animalHelpEntity.setLongitude(animalHelpDTO.getLongitude());
        animalHelpEntity.setTags(animalHelpDTO.getTags());

        if (animalHelpDTO.getImages().stream().filter(AdvertisementImageDTO::getIsPrimary).count() != 1) {
            throw getNeedOnePrimaryImage();
        }
        animalHelpEntity.getImages().forEach(i -> i.setAdvertisement(null));
        animalHelpEntity.getImages().clear();
        animalHelpEntity.setImages(animalHelpDTO.getImages().stream().map(advertisementMapper::advertisementImageEntity).collect(Collectors.toList()));

        repository.save(animalHelpEntity);
    }

    public void deleteAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        repository.deleteByCreatorUserAndId(currentUser, id);
    }

    public void refreshAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        AnimalHelpEntity animalHelpEntity = repository.findByCreatorUserAndId(currentUser, id).orElseThrow(this::getAdvertisementDoesNotExistEx);
        if (!animalHelpEntity.isExpired()){
            throw getAdvertisementNotExpired();
        }
        animalHelpEntity.setExpired(false);
        animalHelpEntity.setCreateDate(LocalDate.now());
        repository.save(animalHelpEntity);
    }

    public BusinessException getAdvertisementDoesNotExistEx() {
        return new BusinessException(ExceptionKeys.ANIMAL_HELP_DOES_NOT_EXIST);
    }

    public BusinessException getNeedOnePrimaryImage() {
        return new BusinessException(ExceptionKeys.NEED_ONE_PRIMARY_IMAGE);
    }

    public BusinessException getAdvertisementNotExpired(){
        return new BusinessException(ExceptionKeys.ADVERTISEMENT_NOT_EXPIRED);
    }
}
