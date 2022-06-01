package ge.edu.freeuni.petcarebackend.controller;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.EmailChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.EmailChangeOtpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PasswordChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.UserDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
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
            @RequestParam(name = "orderBy") @Pattern(regexp = "^[a-zA-Z0-9]{1,50}$") Optional<String> orderBy,
            @RequestParam(name = "asc", required = false) boolean ascending,
            @RequestParam(name = "search", required = false) @Size(min = 1, max = 50) Optional<String> search,
            @RequestParam(name = "type") AdvertisementType type
    ) {
        return service.getMyAdvertisements(
                page, size, orderBy.orElse(null), ascending, search.orElse(null), type
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
