package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.api.dtos.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.api.dtos.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.repo.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.repo.DonationRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import ge.edu.freeuni.petcarebackend.utils.ExceptionKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private AdvertisementImageRepository imageRepository;

    @Autowired
    private SecurityService securityService;

    public DonationEntity getDonationById(long id) throws BusinessException{
        return donationRepository.findById(id).orElseThrow(this::getDonationDoesNotExistEx);
    }

    public List<AdvertisementImageEntity> lookupImages( Long id) {
        DonationEntity donationEntity = getDonationById(id);
        return imageRepository.findByAdvertisement(donationEntity);
    }

    public SearchResultDTO<AdvertisementDTO> search(
            Type type, int page, int size, String orderBy, boolean asc, String search, // header or description
            PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
        return donationRepository.search(
                page, size, orderBy, asc, search,
                type, petType, color, sex,
                ageFrom, ageUntil, breed, city
        );
    }

    public Long createAdvertisement(DonationEntity donationEntity) {
//        TODO set type explicitly, dto wont have type
        UserEntity currentUser = securityService.lookupCurrentUser();
        donationEntity.setCreateDate(LocalDate.now());
        donationEntity.setCreatorUser(currentUser);
        if (donationEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        donationEntity.getImages().forEach(i -> i.setAdvertisement(donationEntity));
        return donationRepository.save(donationEntity).getId();
    }

    public void updateAdvertisement(DonationEntity donationEntity) {
//        TODO set type explicitly, dto wont have type
        UserEntity currentUser = securityService.lookupCurrentUser();
        DonationEntity existing = donationRepository.findByCreatorUserAndId(currentUser,donationEntity.getId())
                .orElseThrow(BusinessException::new);

        existing.setAgeFrom(donationEntity.getAgeFrom());
        existing.setAgeUntil(donationEntity.getAgeUntil());
        existing.setCity(donationEntity.getCity());
        existing.setDescription(donationEntity.getDescription());
        existing.setColor(donationEntity.getColor());
        existing.setHeader(donationEntity.getHeader());
        existing.setLatitude(donationEntity.getLatitude());
        existing.setLongitude(donationEntity.getLongitude());
        existing.setTags(donationEntity.getTags());

        if (donationEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        existing.getImages().forEach(i -> i.setAdvertisement(null));
        existing.getImages().clear();
        existing.setImages(donationEntity.getImages());
        donationRepository.save(existing);
    }

    public void deleteAdvertisement(Long id) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        donationRepository.deleteByCreatorUserAndId(currentUser, id);
    }


    private BusinessException getDonationDoesNotExistEx() {
        return new BusinessException(ExceptionKeys.DONATION_DOES_NOT_EXIST);
    }
}
