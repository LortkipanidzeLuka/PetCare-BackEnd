package ge.edu.freeuni.petcarebackend.security.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user")
    @SequenceGenerator(name = "user", sequenceName = "seq_user", allocationSize = 1)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    @Column(unique = true)
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
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "phone_number")
    @Pattern(regexp = "^5[0-9]{8}$")
    private String phoneNumber;

    @Column(name = "is_verified")
    @JsonIgnore
    private boolean isVerified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
