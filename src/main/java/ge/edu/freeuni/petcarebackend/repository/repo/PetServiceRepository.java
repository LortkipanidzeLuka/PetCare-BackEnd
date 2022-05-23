package ge.edu.freeuni.petcarebackend.repository.repo;

import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetServiceRepository extends JpaRepository<PetServiceEntity, Long> {
}
