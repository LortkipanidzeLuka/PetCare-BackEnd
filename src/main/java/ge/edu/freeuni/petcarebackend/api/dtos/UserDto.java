package ge.edu.freeuni.petcarebackend.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Size(min = 2, max = 32)
    private String firstname;

    @NotNull
    @Size(min = 2, max = 32)
    private String lastname;

    @NotNull
    private SexDto sex;

    @Pattern(regexp = "^5[0-9]{8}$")
    private String phoneNumber;

    private boolean isVerified;
}
