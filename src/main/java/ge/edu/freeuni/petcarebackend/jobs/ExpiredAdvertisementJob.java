package ge.edu.freeuni.petcarebackend.jobs;

import ge.edu.freeuni.petcarebackend.repository.AdvertisementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExpiredAdvertisementJob {

    private final AdvertisementRepository advertisementRepository;

    public ExpiredAdvertisementJob(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    @Scheduled(cron = "0 0 * * *")
    public void softDeleteExpiredAdvertisements() {
//        TODO: soft delete advertisements
    }

}
