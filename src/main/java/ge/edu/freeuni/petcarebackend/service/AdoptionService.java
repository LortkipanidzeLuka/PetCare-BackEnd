package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.AdoptionRepository;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import ge.edu.freeuni.petcarebackend.utils.ExceptionKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AdoptionService {

    private final AdvertisementImageRepository imageRepository;

    private final SecurityService securityService;

    private final AdoptionRepository adoptionRepository;

    public AdoptionService(AdvertisementImageRepository imageRepository, SecurityService securityService, AdoptionRepository adoptionRepository) {
        this.imageRepository = imageRepository;
        this.securityService = securityService;
        this.adoptionRepository = adoptionRepository;
    }

    public AdoptionEntity lookup(Long id) {
        return adoptionRepository.findById(id).orElseThrow(this::getAdoptionDoesNotExistEx);
    }

    public List<AdvertisementImageEntity> lookupImages(Long id) {
        AdoptionEntity adoptionEntity = lookup(id);
        return imageRepository.findByAdvertisement(adoptionEntity);
    }

    public SearchResultDTO<AdvertisementDTO> search(
            int page, int size, String orderBy, boolean asc, String search, // header or description
            PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
        return adoptionRepository.search(
                page, size, orderBy, asc, search,
                petType, color, sex,
                ageFrom, ageUntil, breed, city
        );
    }

    public Long createAdvertisement(AdoptionEntity adoptionEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        adoptionEntity.setCreateDate(LocalDate.now());
        adoptionEntity.setCreatorUser(currentUser);
        adoptionEntity.setAdvertisementType(AdvertisementType.ADOPTION);
        if (adoptionEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        adoptionEntity.getImages().forEach(i -> i.setAdvertisement(adoptionEntity));
        return adoptionRepository.save(adoptionEntity).getId();
    }

    public void updateAdvertisement(AdoptionEntity adoptionEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        AdoptionEntity existing = adoptionRepository.findByCreatorUserAndId(currentUser, adoptionEntity.getId())
                .orElseThrow(this::getAdoptionDoesNotExistEx);

        existing.setAgeFrom(adoptionEntity.getAgeFrom());
        existing.setAgeUntil(adoptionEntity.getAgeUntil());
        existing.setCity(adoptionEntity.getCity());
        existing.setDescription(adoptionEntity.getDescription());
        existing.setBreed(adoptionEntity.getBreed());
        existing.setColor(adoptionEntity.getColor());
        existing.setPetType(adoptionEntity.getPetType());
        existing.setSex(adoptionEntity.getSex());
        existing.setHeader(adoptionEntity.getHeader());
        existing.setLatitude(adoptionEntity.getLatitude());
        existing.setLongitude(adoptionEntity.getLongitude());
        existing.setTags(adoptionEntity.getTags());

        if (adoptionEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        existing.getImages().forEach(i -> i.setAdvertisement(null));
        existing.getImages().clear();
        existing.setImages(adoptionEntity.getImages());

        adoptionRepository.save(existing);
    }

    public void deleteAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        adoptionRepository.deleteByCreatorUserAndId(currentUser, id);
    }

    private BusinessException getAdoptionDoesNotExistEx() {
        return new BusinessException(ExceptionKeys.ADOPTION_DOES_NOT_EXIST);
    }
}
