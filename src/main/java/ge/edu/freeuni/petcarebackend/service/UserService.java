package ge.edu.freeuni.petcarebackend.service;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PasswordChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.UserDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdvertisementRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.OtpService;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
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

    public SearchResultDTO<AdvertisementDTO> getMyAdvertisements(
            int page, int size,
            String orderBy, boolean ascending,
            String search, AdvertisementType type
    ) {
        UserEntity user = securityService.lookupCurrentUser();
        return advertisementRepository.search(page, size, orderBy, ascending, search, type, user);
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
        if (email.equals(currentUser.getUsername())) {
            throw new BusinessException("invalid_email");
        }
        otpService.createAndSendEmailChangeOtp(currentUser, email);
    }

    public AuthorizationTokensDTO changeUserEmailVerify(String email, String otpCode) {
        UserEntity currentUser = securityService.lookupCurrentUser();
        if (otpService.verifyEmailChangeOtpCode(otpCode, currentUser, email)) {
            currentUser.setUsername(email);
            userRepository.save(currentUser);
            return securityService.generateTokens(currentUser);
        } else {
            throw new BusinessException("invalid_otp");
        }
    }

    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        UserEntity user = securityService.lookupCurrentUser();
        if (securityService.validateUserPassword(user, passwordChangeDTO.getOldPassword())) {
            user.setPassword(securityService.encodePassword(passwordChangeDTO.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new BusinessException("invalid_old_password");
        }
    }

}
