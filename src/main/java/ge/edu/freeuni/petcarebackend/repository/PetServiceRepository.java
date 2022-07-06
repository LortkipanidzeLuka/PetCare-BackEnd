package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PetServiceRepository extends JpaRepository<PetServiceEntity, Long>,
        JpaSpecificationExecutor<PetServiceEntity> {

    Optional<PetServiceEntity> findByCreatorUserAndId(UserEntity creatorUser, Long id);

    void deleteByCreatorUserAndId(UserEntity creator, Long id);
}
