package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AdvertisementRepository extends
        JpaRepository<AdvertisementEntity, Long> {

    List<AdvertisementEntity> findByCreateDateBeforeAndExpired(LocalDate createDate, boolean expired);
}
