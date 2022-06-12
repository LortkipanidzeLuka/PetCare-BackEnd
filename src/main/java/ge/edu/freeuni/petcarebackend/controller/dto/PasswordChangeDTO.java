package ge.edu.freeuni.petcarebackend.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PasswordChangeDTO {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String repeatNewPassword;

    @AssertTrue
    @JsonIgnore
    public boolean isValid() {
        return newPassword.equals(repeatNewPassword) && !oldPassword.equals(newPassword);
    }
}
