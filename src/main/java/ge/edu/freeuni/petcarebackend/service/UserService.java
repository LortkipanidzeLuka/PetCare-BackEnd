package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.api.dtos.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.repository.repo.AdvertisementRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final AdvertisementRepository advertisementRepository;

    private final SecurityService securityService;

    public UserService(AdvertisementRepository advertisementRepository,SecurityService securityService) {
        this.advertisementRepository = advertisementRepository;
        this.securityService = securityService;
    }

    public List<AdvertisementDTO> getMyAdvertisements() {
        UserEntity user = securityService.lookupCurrentUser();
        List<AdvertisementEntity> userAdvertisements = advertisementRepository.findByCreatorUser(user);
        return userAdvertisements.stream().map(ad -> new AdvertisementDTO(ad, true)).collect(Collectors.toList());
    }

}
