package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.*;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Optional;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("advertisements")
    public SearchResultDTO<AdvertisementDTO> getMyAdvertisements(
            @RequestParam("page") @Min(1) int page, @RequestParam("size") @Min(5) int size,
            @RequestParam(name = "asc", required = false) boolean ascending,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "type") AdvertisementType type
    ) {
        return service.getMyAdvertisements(
                page, size, ascending, search.orElse(""), type
        );
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
