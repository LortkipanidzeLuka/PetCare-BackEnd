package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertisementImageRepository extends JpaRepository<AdvertisementImageEntity, Long> {

    List<AdvertisementImageEntity> findByAdvertisement(AdvertisementEntity advertisement);

}
