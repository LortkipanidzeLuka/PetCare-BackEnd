package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.repository.entity.AnimalHelpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface AnimalHelpRepository extends JpaRepository<AnimalHelpEntity, Long>,
        QuerydslPredicateExecutor<AnimalHelpEntity> {

    void deleteByCreatorUserAndId(UserEntity creator, Long id);

    Optional<AnimalHelpEntity> findByCreatorUserAndId(UserEntity creatorUser, Long id);

}
