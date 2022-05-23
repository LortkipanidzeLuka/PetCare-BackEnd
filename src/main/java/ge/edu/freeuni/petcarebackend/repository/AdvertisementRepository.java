package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long>, JpaSpecificationExecutor<AdvertisementEntity> {

    List<AdvertisementEntity> findByCreatorUser(UserEntity user);

}
