package ge.edu.freeuni.petcarebackend.security.controller;

import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("security")
public class SecurityController {

    private final SecurityService service;

    public SecurityController(SecurityService service) {
        this.service = service;
    }

    @PostMapping("register")
    public void registerUser() {
        service.register();
    }

    @PostMapping("authorize")
    public String authorizeUSer() {
        return service.authorize();
    }

    @PostMapping
    public void logoutUser() {
        service.logout();
    }

}
