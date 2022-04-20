package ge.edu.freeuni.petcarebackend.security.service;

import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.AuthUserDetails;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository repository;

    public SecurityService(UserRepository repository) {
        this.repository = repository;
    }

    public UserEntity lookupCurrentUser() {
        return ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).user();
    }

    public void register() {
//        TODO
    }

    public String authorize() {
//      TODO
        return null;
    }

    public void logout() {
//      TODO
    }

}
