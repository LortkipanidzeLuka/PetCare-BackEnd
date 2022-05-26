package ge.edu.freeuni.petcarebackend.api.controller;

import ge.edu.freeuni.petcarebackend.api.dtos.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
