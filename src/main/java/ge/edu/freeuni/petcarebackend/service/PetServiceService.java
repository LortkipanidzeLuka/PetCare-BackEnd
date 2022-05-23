package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
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

import java.util.List;

@Service
public class PetServiceService {

    @Autowired
    private PetServiceRepository petServiceRepository;

    @Autowired
    private AdvertisementImageRepository imageRepository;

    @Autowired
    private SecurityService securityService;

    public PetServiceEntity getPetServiceById(long id) throws BusinessException {
        return petServiceRepository.findById(id).orElseThrow(this::getDonationDoesNotExistEx);
    }

    public List<AdvertisementImageEntity> lookupImages(Long id) {
        PetServiceEntity petServiceEntity = getPetServiceById(id);
        return imageRepository.findByAdvertisement(petServiceEntity);
    }

    public SearchResultDTO<PetServiceEntity> search(
            Type type, int page, int size, String orderBy, boolean asc, String search, // header or description
            PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
//        return donationRepository.search(
//                page, size, orderBy, asc, search,
//                type, petType, color, sex,
//                ageFrom, ageUntil, breed, city
//        );
        return null;
    }

    public Long createAdvertisement(PetServiceEntity petServiceEntity) {
//        TODO set type explicitly, dto wont have type
        UserEntity currentUser = securityService.lookupCurrentUser();
//        (LocalDate.now());
//        donationEntity.setCreatorUser(currentUser);
//        if (donationEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
//            throw new BusinessException("need_one_primary_image");
//        }
//        lostFoundEntity.getImages().forEach(i -> i.setAdvertisement(lostFoundEntity));
//        return repository.save(lostFoundEntity).getId();
        return null;
    }

    public void updateAdvertisement(PetServiceEntity petServiceEntity) {
//        TODO set type explicitly, dto wont have type
//        UserEntity currentUser = securityService.lookupCurrentUser();
//        LostFoundEntity lostFoundEntity = repository.findByCreatorUserAndId(currentUser, id).orElseThrow(BusinessException::new);
//
//        lostFoundEntity.setAgeFrom(lostFoundDTO.getAgeFrom());
//        lostFoundEntity.setAgeUntil(lostFoundDTO.getAgeUntil());
//        lostFoundEntity.setCity(lostFoundDTO.getCity());
//        lostFoundEntity.setDescription(lostFoundDTO.getDescription());
//        lostFoundEntity.setBreed(lostFoundDTO.getBreed());
//        lostFoundEntity.setColor(lostFoundDTO.getColor());
//        lostFoundEntity.setSex(lostFoundDTO.getSex());
//        lostFoundEntity.setPetType(lostFoundDTO.getPetType());
//        lostFoundEntity.setHeader(lostFoundEntity.getHeader());
//        lostFoundEntity.setLatitude(lostFoundDTO.getLatitude());
//        lostFoundEntity.setLongitude(lostFoundDTO.getLongitude());
//        lostFoundEntity.setTags(lostFoundDTO.getTags());
//
//        if (lostFoundDTO.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
//            throw new BusinessException("need_one_primary_image");
//        }
//        lostFoundEntity.getImages().forEach(i -> i.setAdvertisement(null));
//        lostFoundEntity.getImages().clear();
//        lostFoundEntity.setImages(lostFoundDTO.getImages());
        return;
        //     repository.save(lostFoundEntity);
    }

    public void deleteAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        //   donationRepository.deleteByCreatorUserAndTypeAndId(currentUser, type, id);
        return;
    }


    private BusinessException getDonationDoesNotExistEx() {
        return new BusinessException(ExceptionKeys.DONATION_DOES_NOT_EXIST);
    }
}
