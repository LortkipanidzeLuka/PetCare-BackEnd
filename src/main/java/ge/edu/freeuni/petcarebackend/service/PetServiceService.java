package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.api.dtos.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.api.dtos.PetServiceTypeDto;
import ge.edu.freeuni.petcarebackend.api.dtos.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.api.dtos.SexDto;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.repo.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.repo.PetServiceRepository;
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
public class PetServiceService {

    @Autowired
    private PetServiceRepository petServiceRepository;

    @Autowired
    private AdvertisementImageRepository imageRepository;

    @Autowired
    private SecurityService securityService;

    public PetServiceEntity getPetServiceById(long id) throws BusinessException {
        return petServiceRepository.findById(id).orElseThrow(this::getPetServiceDoesNotExistEx);
    }

    public List<AdvertisementImageEntity> lookupImages(Long id) {
        PetServiceEntity petServiceEntity = getPetServiceById(id);
        return imageRepository.findByAdvertisement(petServiceEntity);
    }

    public SearchResultDTO<AdvertisementDTO> search(
            int page, int size, String orderBy, boolean asc, String search,
            PetServiceTypeDto petServiceType, Color color, SexDto applicableSex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
        return petServiceRepository.search(
                page, size, orderBy, asc, search,
                petServiceType, color, applicableSex,
                ageFrom, ageUntil, breed, city
        );
    }

    public Long createAdvertisement(PetServiceEntity petServiceEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        petServiceEntity.setCreateDate(LocalDate.now());
        petServiceEntity.setCreatorUser(currentUser);
        if (petServiceEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        petServiceEntity.setAdvertisementType(AdvertisementType.PET_SERVICE);
        petServiceEntity.getImages().forEach(i -> i.setAdvertisement(petServiceEntity));
        return petServiceRepository.save(petServiceEntity).getId();
    }

    public void updateAdvertisement(PetServiceEntity petServiceEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        PetServiceEntity existing = petServiceRepository.findByCreatorUserAndId(currentUser, petServiceEntity.getId())
                .orElseThrow(BusinessException::new);

        existing.setAgeFrom(petServiceEntity.getAgeFrom());
        existing.setAgeUntil(petServiceEntity.getAgeUntil());
        existing.setCity(petServiceEntity.getCity());
        existing.setDescription(petServiceEntity.getDescription());
        existing.setHeader(petServiceEntity.getHeader());
        existing.setLatitude(petServiceEntity.getLatitude());
        existing.setLongitude(petServiceEntity.getLongitude());
        existing.setTags(petServiceEntity.getTags());
        existing.setApplicableSex(petServiceEntity.getApplicableSex());
        existing.setPetServiceType(petServiceEntity.getPetServiceType());


        if (petServiceEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        existing.getImages().forEach(i -> i.setAdvertisement(null));
        existing.getImages().clear();
        existing.setImages(petServiceEntity.getImages());
        petServiceRepository.save(existing);
    }

    public void deleteAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        petServiceRepository.deleteByCreatorUserAndId(currentUser, id);
    }


    private BusinessException getPetServiceDoesNotExistEx() {
        return new BusinessException(ExceptionKeys.PET_SERVICE_DOES_NOT_EXIST);
    }
}
