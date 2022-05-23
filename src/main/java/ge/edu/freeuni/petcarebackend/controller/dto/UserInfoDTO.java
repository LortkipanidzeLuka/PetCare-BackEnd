package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDTO {

    private String username;

    private String fullName;

    private String phoneNumber;

    public UserInfoDTO(UserEntity user) {
        this.username = user.getUsername();
        this.fullName = user.getFirstname() + " " + user.getLastname();
        this.phoneNumber = user.getPhoneNumber();
    }
}
