package ge.edu.freeuni.petcarebackend.security.repository;

import ge.edu.freeuni.petcarebackend.security.repository.entity.OtpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findByUserAndCode(UserEntity user, String code);

    long countByUserAndUsedAndCreateTsIsAfter(UserEntity user, boolean used, LocalDateTime createTs);

}
