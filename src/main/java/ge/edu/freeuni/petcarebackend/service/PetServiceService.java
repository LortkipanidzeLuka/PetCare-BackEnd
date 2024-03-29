package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.PetServiceRepository;
import ge.edu.freeuni.petcarebackend.repository.PetServiceSearchRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import ge.edu.freeuni.petcarebackend.exception.ExceptionKeys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PetServiceService {

    private final PetServiceRepository petServiceRepository;

    private final AdvertisementImageRepository imageRepository;

    private final SecurityService securityService;

    private final PetServiceSearchRepository petServiceSearchRepository;

    public PetServiceService(PetServiceRepository petServiceRepository, AdvertisementImageRepository imageRepository, SecurityService securityService, PetServiceSearchRepository petServiceSearchRepository) {
        this.petServiceRepository = petServiceRepository;
        this.imageRepository = imageRepository;
        this.securityService = securityService;
        this.petServiceSearchRepository = petServiceSearchRepository;
    }


    public PetServiceEntity getPetServiceById(long id) throws BusinessException {
        return petServiceRepository.findById(id).orElseThrow(this::getPetServiceDoesNotExistEx);
    }

    public List<AdvertisementImageEntity> lookupImages(Long id) {
        PetServiceEntity petServiceEntity = getPetServiceById(id);
        return imageRepository.findByAdvertisement(petServiceEntity);
    }

    public SearchResultDTO<PetServiceDTO> search(
            int page, int size, String search,
            PetServiceType petServiceType, String breed, City city,
            BigDecimal longitude, BigDecimal latitude
    ) {
        return petServiceSearchRepository.search(
                page, size, search,
                petServiceType, breed, city,
                longitude, latitude
        );
    }

    public Long createAdvertisement(PetServiceEntity petServiceEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        petServiceEntity.setCreateDate(LocalDate.now());
        petServiceEntity.setCreatorUser(currentUser);
        if (petServiceEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw getNeedOnePrimaryImage();
        }
        petServiceEntity.setAdvertisementType(AdvertisementType.PET_SERVICE);
        petServiceEntity.getImages().forEach(i -> i.setAdvertisement(petServiceEntity));
        return petServiceRepository.save(petServiceEntity).getId();
    }

    public void updateAdvertisement(PetServiceEntity petServiceEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        PetServiceEntity existing = petServiceRepository.findByCreatorUserAndId(currentUser, petServiceEntity.getId())
                .orElseThrow(BusinessException::new);

        existing.setCity(petServiceEntity.getCity());
        existing.setDescription(petServiceEntity.getDescription());
        existing.setHeader(petServiceEntity.getHeader());
        existing.setLatitude(petServiceEntity.getLatitude());
        existing.setLongitude(petServiceEntity.getLongitude());
        existing.setTags(petServiceEntity.getTags());
        existing.setPetServiceType(petServiceEntity.getPetServiceType());
        existing.setApplicablePetList(petServiceEntity.getApplicablePetList());

        if (petServiceEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw getNeedOnePrimaryImage();
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

    public void refreshAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        PetServiceEntity petServiceEntity = petServiceRepository.findByCreatorUserAndId(currentUser, id).orElseThrow(this::getPetServiceDoesNotExistEx);
        if (!petServiceEntity.isExpired()){
            throw getAdvertisementNotExpired();
        }
        petServiceEntity.setExpired(false);
        petServiceEntity.setCreateDate(LocalDate.now());
        petServiceRepository.save(petServiceEntity);
    }

    private BusinessException getPetServiceDoesNotExistEx() {
        return new BusinessException(ExceptionKeys.PET_SERVICE_DOES_NOT_EXIST);
    }

    public BusinessException getNeedOnePrimaryImage() {
        return new BusinessException(ExceptionKeys.NEED_ONE_PRIMARY_IMAGE);
    }

    public BusinessException getAdvertisementNotExpired(){
        return new BusinessException(ExceptionKeys.ADVERTISEMENT_NOT_EXPIRED);
    }
}
