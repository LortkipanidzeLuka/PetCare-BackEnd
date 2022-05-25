package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.UserDTO;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.OtpService;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final AdvertisementRepository advertisementRepository;

    private final SecurityService securityService;

    private final OtpService otpService;

    public UserService(UserRepository userRepository, AdvertisementRepository advertisementRepository, SecurityService securityService, OtpService otpService) {
        this.userRepository = userRepository;
        this.advertisementRepository = advertisementRepository;
        this.securityService = securityService;
        this.otpService = otpService;
    }

    public List<AdvertisementDTO> getMyAdvertisements() {
        UserEntity user = securityService.lookupCurrentUser();
        List<AdvertisementEntity> userAdvertisements = advertisementRepository.findByCreatorUser(user);
        return userAdvertisements.stream().map(ad -> new AdvertisementDTO(ad, true)).collect(Collectors.toList());
    }

    public UserDTO getUserInfo() {
        UserEntity user = securityService.lookupCurrentUser();
        return new UserDTO(user);
    }

    public void changeUserInfo(UserDTO userDTO) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        currentUser.setFirstname(userDTO.getFirstname());
        currentUser.setLastname(userDTO.getLastname());
        currentUser.setSex(userDTO.getSex());
        currentUser.setPhoneNumber(userDTO.getPhoneNumber());
        userRepository.save(currentUser);
    }

    public void changeUserEmailSendCode(String email) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        otpService.createAndSendEmailChangeOtp(currentUser, email);
    }

    public void changeUserEmailVerify(String email, String otpCode) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        if (otpService.verifyEmailChangeOtpCode(otpCode, currentUser, email)) {
            currentUser.setUsername(email);
        }
        userRepository.save(currentUser);
    }

}
