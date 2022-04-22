package ge.edu.freeuni.petcarebackend.security.service;

import com.nulabinc.zxcvbn.Zxcvbn;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.LoginDTO;
import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.AuthUserDetails;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
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

    private final UserRepository repository;

    private final JwtTokenService tokenService;

    @Value("${zxcvbn.password.strength}")
    private int ZXCVBN_PASSWORD_STRENGTH;

    public SecurityService(UserRepository repository, JwtTokenService tokenService) {
        this.repository = repository;
        this.tokenService = tokenService;
    }

    public UserEntity lookupCurrentUser() {
        return ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).user();
    }

    public void register(UserEntity user) {
        if (new Zxcvbn().measure(user.getPassword()).getScore() < ZXCVBN_PASSWORD_STRENGTH) {
            throw new RuntimeException("weak_password"); // TODO: change to custom exception
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        try {
            repository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("email_used"); // TODO: change to custom exception
        }
    }

    public AuthorizationTokensDTO login(LoginDTO loginDTO) {
        Optional<UserEntity> user = repository.findByUsername(loginDTO.username());
        if (user.isPresent() && new BCryptPasswordEncoder().matches(loginDTO.password(), user.get().getPassword())) {
            return tokenService.generateTokens(user.get());
        }
        throw new RuntimeException("invalid username or password"); // TODO: change
    }

    public AuthorizationTokensDTO authenticateWithRefreshToken(String refreshToken) {
        if (!tokenService.isTokenExpired(refreshToken) && tokenService.isRefreshToken(refreshToken)) {
            String username = tokenService.extractUsername(refreshToken);
            Optional<UserEntity> user = repository.findByUsername(username);
            if (user.isPresent()) {
                return tokenService.generateTokens(user.get());
            }
        }
        throw new RuntimeException("invalid refresh token"); // TODO: change
    }
}
