package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.LostFoundRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundType;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LostFoundService {

    private final LostFoundRepository repository;

    private final AdvertisementImageRepository imageRepository;

    private final SecurityService securityService;

    public LostFoundService(LostFoundRepository repository, AdvertisementImageRepository imageRepository, SecurityService securityService) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.securityService = securityService;
    }

    public LostFoundDTO lookupAdvertisement(LostFoundType type, Long id) {
        return repository.findByTypeAndId(type, id).map(ad -> new LostFoundDTO(ad, false)).orElseThrow(BusinessException::new);
    }

    public LostFoundEntity lookup(LostFoundType type, Long id) {
        return repository.findByTypeAndId(type, id).orElseThrow(BusinessException::new);
    }

    public List<AdvertisementImageEntity> lookupImages(LostFoundType type, Long id) {
        LostFoundEntity lostFoundEntity = lookup(type, id);
        return imageRepository.findByAdvertisement(lostFoundEntity);
    }

    public SearchResultDTO<AdvertisementDTO> search(
            LostFoundType type, int page, int size, String orderBy, boolean asc, String search, // header or description
            PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
        return repository.search(
                page, size, orderBy, asc, search,
                type, petType, color, sex,
                ageFrom, ageUntil, breed, city
        );
    }

    public Long createAdvertisement(LostFoundType type, LostFoundEntity lostFoundEntity) {
//        TODO set type explicitly, dto wont have type
        UserEntity currentUser = securityService.lookupCurrentUser();
        lostFoundEntity.setCreateDate(LocalDate.now());
        lostFoundEntity.setCreatorUser(currentUser);
        lostFoundEntity.setAdvertisementType(AdvertisementType.LOST_FOUND);
        if (lostFoundEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        lostFoundEntity.getImages().forEach(i -> i.setAdvertisement(lostFoundEntity));
        return repository.save(lostFoundEntity).getId();
    }

    public void updateAdvertisement(LostFoundType type, Long id, LostFoundEntity lostFoundDTO) {
//        TODO set type explicitly, dto wont have type
        UserEntity currentUser = securityService.lookupCurrentUser();
        LostFoundEntity lostFoundEntity = repository.findByCreatorUserAndId(currentUser, id).orElseThrow(BusinessException::new);

        lostFoundEntity.setAgeFrom(lostFoundDTO.getAgeFrom());
        lostFoundEntity.setAgeUntil(lostFoundDTO.getAgeUntil());
        lostFoundEntity.setCity(lostFoundDTO.getCity());
        lostFoundEntity.setDescription(lostFoundDTO.getDescription());
        lostFoundEntity.setBreed(lostFoundDTO.getBreed());
        lostFoundEntity.setColor(lostFoundDTO.getColor());
        lostFoundEntity.setSex(lostFoundDTO.getSex());
        lostFoundEntity.setPetType(lostFoundDTO.getPetType());
        lostFoundEntity.setHeader(lostFoundEntity.getHeader());
        lostFoundEntity.setLatitude(lostFoundDTO.getLatitude());
        lostFoundEntity.setLongitude(lostFoundDTO.getLongitude());
        lostFoundEntity.setTags(lostFoundDTO.getTags());

        if (lostFoundDTO.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        lostFoundEntity.getImages().forEach(i -> i.setAdvertisement(null));
        lostFoundEntity.getImages().clear();
        lostFoundEntity.setImages(lostFoundDTO.getImages());

        repository.save(lostFoundEntity);
    }

    public void deleteAdvertisement(LostFoundType type, Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        repository.deleteByCreatorUserAndTypeAndId(currentUser, type, id);
    }

}
