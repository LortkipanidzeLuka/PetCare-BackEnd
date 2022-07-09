package ge.edu.freeuni.petcarebackend.jobs;

import ge.edu.freeuni.petcarebackend.repository.AdvertisementRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ExpiredAdvertisementJob {

    private final AdvertisementRepository repository;

    public ExpiredAdvertisementJob(AdvertisementRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void softDeleteExpiredAdvertisements() {
        LocalDate lastValidDate = LocalDate.now().minusDays(30);
        List<AdvertisementEntity> expiredAdvertisements = repository.findByCreateDateBeforeAndExpired(lastValidDate, false);
        expiredAdvertisements.forEach(adv -> adv.setExpired(true));
    }

}
