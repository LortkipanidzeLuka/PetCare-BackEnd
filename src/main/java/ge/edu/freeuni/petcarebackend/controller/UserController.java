package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.EmailChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.EmailChangeOtpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PasswordChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.UserDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("advertisements")
    public List<AdvertisementDTO> getMyAdvertisements() {
        return service.getMyAdvertisements();
    }

    @GetMapping("info")
    public UserDTO getUserInfo() {
        return service.getUserInfo();
    }

    @PutMapping("info")
    public void changeUserInfo(@Valid @RequestBody UserDTO userDTO) {
        service.changeUserInfo(userDTO);
    }

    @PostMapping("email/change/code")
    public void changeUserEmailSendCode(@Valid @RequestBody EmailChangeDTO emailChangeDTO) {
        service.changeUserEmailSendCode(emailChangeDTO.getEmail());
    }

    @PostMapping("email/change/code/verify")
    public AuthorizationTokensDTO changeUserEmailVerify(@Valid @RequestBody EmailChangeOtpDTO emailChangeDTO) {
        return service.changeUserEmailVerify(emailChangeDTO.getEmail(), emailChangeDTO.getCode());
    }

    @PutMapping("password")
    public void changePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        service.changePassword(passwordChangeDTO);
    }

}
