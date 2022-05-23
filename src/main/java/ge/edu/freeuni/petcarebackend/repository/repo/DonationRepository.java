package ge.edu.freeuni.petcarebackend.repository.repo;

import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, Long> {
}
