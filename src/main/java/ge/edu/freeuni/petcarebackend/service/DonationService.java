package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.*;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementImageRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.DonationRepository;
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
public class DonationService {

    private final DonationRepository donationRepository;

    private final AdvertisementImageRepository imageRepository;

    private final SecurityService securityService;

    public DonationService(DonationRepository donationRepository, AdvertisementImageRepository imageRepository, SecurityService securityService) {
        this.donationRepository = donationRepository;
        this.imageRepository = imageRepository;
        this.securityService = securityService;
    }

    public DonationEntity getDonationById(long id) throws BusinessException{
        return donationRepository.findById(id).orElseThrow(this::getDonationDoesNotExistEx);
    }

    public List<AdvertisementImageEntity> lookupImages( Long id) {
        DonationEntity donationEntity = getDonationById(id);
        return imageRepository.findByAdvertisement(donationEntity);
    }

    public Long createAdvertisement(DonationEntity donationEntity) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        donationEntity.setCreateDate(LocalDate.now());
        donationEntity.setCreatorUser(currentUser);
        donationEntity.setAdvertisementType(AdvertisementType.DONATION);
        if (donationEntity.getImages().stream().filter(AdvertisementImageEntity::getIsPrimary).count() != 1) {
            throw new BusinessException("need_one_primary_image");
        }
        donationEntity.getImages().forEach(i -> i.setAdvertisement(donationEntity));
        return donationRepository.save(donationEntity).getId();
    }

    public SearchResultDTO<AdvertisementDTO> search(int page, int size, String orderBy, boolean asc,
                                                    String search, DonationAdvertisementType donationAdvertisementType,
                                                    Color color, Sex applicableSex, Integer ageFrom, Integer ageUntil,
                                                    City city) {

        return donationRepository.search(
                page, size, orderBy, asc, search,
                donationAdvertisementType, color, applicableSex,
                ageFrom, ageUntil, city
        );
    }

    public void updateAdvertisement(DonationEntity donationEntity) {
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
        existing.setColor(donationEntity.getColor());
        existing.setDonationAdvertisementType(donationEntity.getDonationAdvertisementType());
        existing.setApplicableSex(donationEntity.getApplicableSex());


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
