package ge.edu.freeuni.petcarebackend.security.service;

import ge.edu.freeuni.petcarebackend.security.RandomStringGenerator;
import ge.edu.freeuni.petcarebackend.security.repository.OtpRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.OtpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpRepository repository;

    private final RandomStringGenerator codeGenerator = new RandomStringGenerator(6, new SecureRandom(), RandomStringGenerator.DIGITS);

    public OtpService(OtpRepository repository) {
        this.repository = repository;
    }

    public void createAndSendOtp(UserEntity user) {
        OtpEntity otp = createOtp(user);
//        TODO: send via mail sender service
    }

    private OtpEntity createOtp(UserEntity user) {
        OtpEntity otp = new OtpEntity();
        otp.setCode(codeGenerator.nextString());
        otp.setCreateTs(LocalDateTime.now());
        otp.setValidUntil(LocalDateTime.now().plusMinutes(1));
        otp.setUsed(false);
        otp.setUser(user);
        return repository.save(otp);
    }

    public boolean verifyOtpCode(String code, UserEntity user) {
        Optional<OtpEntity> otp = repository.findByUserAndCode(user, code);
        boolean isValid = otp.isPresent() && !otp.get().isUsed() && otp.get().getValidUntil().isAfter(LocalDateTime.now());
        if (isValid) {
            otp.get().setUsed(true);
            return true;
        }
        return false;
    }

    public void resendOtpCode(UserEntity user) {
        long countUnusedOtpsWithin5Minutes = repository.countByUserAndUsedAndCreateTsIsAfter(user, false, LocalDateTime.now().minusMinutes(5));
        if (countUnusedOtpsWithin5Minutes >= 3) {
            throw new RuntimeException("otp_retries_exceeded");
        }
        createAndSendOtp(user);
    }
}
