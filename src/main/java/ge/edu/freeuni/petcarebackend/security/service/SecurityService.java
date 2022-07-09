package ge.edu.freeuni.petcarebackend.security.service;

import com.nulabinc.zxcvbn.Zxcvbn;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.LoginDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.OtpDTO;
import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.AuthUserDetails;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.exception.ExceptionKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SecurityService {

    private final JwtTokenService tokenService;

    private final OtpService otpService;

    private final UserRepository repository;

    @Value("${zxcvbn.password.strength}")
    private int ZXCVBN_PASSWORD_STRENGTH;

    public SecurityService(UserRepository repository, JwtTokenService tokenService, OtpService otpService) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.otpService = otpService;
    }

    public UserEntity lookupCurrentUser() {
        return ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).user();
    }

    public void register(UserEntity user) {
        user.setPassword(encodePassword(user.getPassword()));
        try {
            repository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ExceptionKeys.EMAIL_USED);
        }
    }

    public AuthorizationTokensDTO login(LoginDTO loginDTO) {
        Optional<UserEntity> user = repository.findByUsername(loginDTO.username());
        if (user.isPresent() && validateUserPassword(user.get(), loginDTO.password())) {
            return generateTokens(user.get());
        }
        throw new BusinessException(ExceptionKeys.INVALID_CREDENTIALS);
    }

    public AuthorizationTokensDTO authenticateWithRefreshToken(String refreshToken) {
        if (!tokenService.isTokenExpired(refreshToken) && tokenService.isRefreshToken(refreshToken)) {
            String username = tokenService.extractUsername(refreshToken);
            Optional<UserEntity> user = repository.findByUsername(username);
            if (user.isPresent()) {
                return tokenService.generateTokens(user.get());
            }
        }
        throw new BusinessException(ExceptionKeys.INVALID_REFRESH_TOKEN);
    }

    public void verifyOtpCode(OtpDTO otp) {
        boolean correctOtp = otpService.verifyOtpCode(otp.getCode(), lookupCurrentUser());
        if (correctOtp) {
            UserEntity user = lookupCurrentUser();
            user.setVerified(true);
            repository.save(user);
            return;
        }
        throw new BusinessException(ExceptionKeys.INVALID_OTP);
    }

    public void resendCode() {
        otpService.resendOtpCode(lookupCurrentUser());
    }

    public boolean validateUserPassword(UserEntity user, String password) {
        return new BCryptPasswordEncoder().matches(password, user.getPassword());
    }

    public String encodePassword(String password) {
        if (new Zxcvbn().measure(password).getScore() < ZXCVBN_PASSWORD_STRENGTH) {
            throw new BusinessException(ExceptionKeys.WEAK_PASSWORD);
        }
        return new BCryptPasswordEncoder().encode(password);
    }

    public AuthorizationTokensDTO generateTokens(UserEntity user) {
        return tokenService.generateTokens(user);
    }

}
