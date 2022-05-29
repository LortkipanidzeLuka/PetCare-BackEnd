package ge.edu.freeuni.petcarebackend.security.repository;

import ge.edu.freeuni.petcarebackend.security.repository.entity.EmailChangeOtpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailChangeOtpRepository extends JpaRepository<EmailChangeOtpEntity, Long> {

    Optional<EmailChangeOtpEntity> findByUserAndCodeAndEmail(UserEntity user, String code, String email);

}
