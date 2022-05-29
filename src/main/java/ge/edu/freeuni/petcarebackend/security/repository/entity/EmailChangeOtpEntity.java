package ge.edu.freeuni.petcarebackend.security.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "email_change_otp")
public class EmailChangeOtpEntity extends OtpEntity {

    private String email;

}
