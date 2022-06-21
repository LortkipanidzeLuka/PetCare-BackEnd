package ge.edu.freeuni.petcarebackend.security.controller;

import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.LoginDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.OtpDTO;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class SecurityController {

    private final SecurityService service;

    public SecurityController(SecurityService service) {
        this.service = service;
    }

    @PostMapping("register")
    public void registerUser(@Valid @RequestBody UserEntity user) {
        service.register(user);
    }

    @PostMapping("login")
    public ResponseEntity<AuthorizationTokensDTO> loginUser(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(service.login(loginDTO));
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthorizationTokensDTO> refreshAccess(@RequestBody AuthorizationTokensDTO refreshToken) {
        return ResponseEntity.ok(service.authenticateWithRefreshToken(refreshToken.refreshToken()));
    }

    @PostMapping("verify")
    public void verify(@RequestBody OtpDTO otp) {
        service.verifyOtpCode(otp);
    }

    @PostMapping("verify/resend")
    public void resendCode() {
        service.resendCode();
    }

}
