package ge.edu.freeuni.petcarebackend.security.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "otp")
@Inheritance(strategy = InheritanceType.JOINED)
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otp")
    @SequenceGenerator(name = "otp", sequenceName = "seq_otp", allocationSize = 1)
    private Long id;

    private String code;

    @Column(name = "create_ts")
    private LocalDateTime createTs;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    private boolean used;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpEntity otpEntity = (OtpEntity) o;
        return Objects.equals(id, otpEntity.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
