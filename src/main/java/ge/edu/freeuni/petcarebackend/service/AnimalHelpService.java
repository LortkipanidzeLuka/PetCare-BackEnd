package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.mapper.AdvertisementMapper;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.AnimalHelpRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AnimalHelpService(AnimalHelpRepository repository, AdvertisementImageRepository imageRepository, SecurityService securityService, AdvertisementMapper advertisementMapper) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.securityService = securityService;
        this.advertisementMapper = advertisementMapper;
    }

    public LostFoundDTO lookupAdvertisement(Long id) {
        return repository.findById(id).map(ad -> new LostFoundDTO(ad, false)).orElseThrow(BusinessException::new);
    }

    public AnimalHelpEntity lookup(Long id) {
        return repository.findById(id).orElseThrow(BusinessException::new);
    }

    public List<AdvertisementImageDTO> lookupImages(Long id) {
        AnimalHelpEntity animalHelpEntity = lookup(id);
        return imageRepository.findByAdvertisement(animalHelpEntity).stream().map(AdvertisementImageDTO::new).collect(Collectors.toList());
    }

    public SearchResultDTO<AdvertisementDTO> search(
            AnimalHelpType type, int page, int size, String orderBy, boolean asc, String search, // header or description
            PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
        return repository.search(
                page, size, orderBy, asc, search,
                type, petType, color, sex,
                ageFrom, ageUntil, breed, city
        );
    }

    public Long createAdvertisement(AnimalHelpEntity animalHelpEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        animalHelpEntity.setCreateDate(LocalDate.now());
        animalHelpEntity.setCreatorUser(currentUser);
        animalHelpEntity.setAdvertisementType(AdvertisementType.LOST_FOUND);
        if (animalHelpEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        animalHelpEntity.getImages().forEach(i -> i.setAdvertisement(animalHelpEntity));
        return repository.save(animalHelpEntity).getId();
    }

    public void updateAdvertisement(Long id, LostFoundDTO lostFoundDTO) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        AnimalHelpEntity animalHelpEntity = repository.findByCreatorUserAndId(currentUser, id).orElseThrow(BusinessException::new);

        animalHelpEntity.setAgeFrom(lostFoundDTO.getAgeFrom());
        animalHelpEntity.setAgeUntil(lostFoundDTO.getAgeUntil());
        animalHelpEntity.setCity(lostFoundDTO.getCity());
        animalHelpEntity.setDescription(lostFoundDTO.getDescription());
        animalHelpEntity.setBreed(lostFoundDTO.getBreed());
        animalHelpEntity.setColor(lostFoundDTO.getColor());
        animalHelpEntity.setSex(lostFoundDTO.getSex());
        animalHelpEntity.setPetType(lostFoundDTO.getPetType());
        animalHelpEntity.setHeader(lostFoundDTO.getHeader());
        animalHelpEntity.setLatitude(lostFoundDTO.getLatitude());
        animalHelpEntity.setLongitude(lostFoundDTO.getLongitude());
        animalHelpEntity.setTags(lostFoundDTO.getTags());

        if (lostFoundDTO.getImages().stream().filter(AdvertisementImageDTO::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        animalHelpEntity.getImages().forEach(i -> i.setAdvertisement(null));
        animalHelpEntity.getImages().clear();
        animalHelpEntity.setImages(lostFoundDTO.getImages().stream().map(advertisementMapper::advertisementImageEntity).collect(Collectors.toList()));

        repository.save(animalHelpEntity);
    }

    public void deleteAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        repository.deleteByCreatorUserAndId(currentUser, id);
    }
}
