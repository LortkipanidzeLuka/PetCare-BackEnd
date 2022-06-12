package ge.edu.freeuni.petcarebackend.controller.dto;

import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotNull
    private Sex sex;

    @Pattern(regexp = "^5[0-9]{8}$")
    private String phoneNumber;

    public UserDTO(UserEntity user) {
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.sex = user.getSex();
        this.phoneNumber = user.getPhoneNumber();
    }

}
