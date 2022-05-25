package ge.edu.freeuni.petcarebackend.security.service;

import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.security.RandomStringGenerator;
import ge.edu.freeuni.petcarebackend.security.repository.EmailChangeOtpRepository;
import ge.edu.freeuni.petcarebackend.security.repository.OtpRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.EmailChangeOtpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.OtpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.service.MailSenderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OtpService {

    private final OtpRepository repository;

    private final EmailChangeOtpRepository emailChangeOtpRepository;

    private final RandomStringGenerator codeGenerator = new RandomStringGenerator(6, new SecureRandom(), RandomStringGenerator.DIGITS);

    private final MailSenderService mailSenderService;

    public OtpService(OtpRepository repository, EmailChangeOtpRepository emailChangeOtpRepository, MailSenderService mailSenderService) {
        this.repository = repository;
        this.emailChangeOtpRepository = emailChangeOtpRepository;
        this.mailSenderService = mailSenderService;
    }

    public void createAndSendOtp(UserEntity user) {
        OtpEntity otp = createOtp(user);
        mailSenderService.sendMail(user.getUsername(), "ერთჯერადი კოდი", "ერთჯერადი კოდი: %s \n ვადა: 60 წამი\n".formatted(otp.getCode()));
    }

    public void createAndSendEmailChangeOtp(UserEntity user, String email) {
        EmailChangeOtpEntity otp = createEmailChangeOtp(user, email);
        mailSenderService.sendMail(email, "ერთჯერადი კოდი", "ერთჯერადი კოდი: %s \n ვადა: 60 წამი\n".formatted(otp.getCode()));
    }

    private EmailChangeOtpEntity createEmailChangeOtp(UserEntity user, String email) {
        EmailChangeOtpEntity otp = new EmailChangeOtpEntity();
        otp.setCode(codeGenerator.nextString());
        otp.setCreateTs(LocalDateTime.now());
        otp.setValidUntil(LocalDateTime.now().plusMinutes(1));
        otp.setUsed(false);
        otp.setUser(user);
        otp.setEmail(email);
        return repository.save(otp);
    }

    public boolean verifyEmailChangeOtpCode(String code, UserEntity user, String email) {
        Optional<EmailChangeOtpEntity> otp = emailChangeOtpRepository.findByUserAndCodeAndEmail(user, code, email);
        boolean isValid = otp.isPresent() && !otp.get().isUsed() && otp.get().getValidUntil().isAfter(LocalDateTime.now());
        if (isValid) {
            otp.get().setUsed(true);
            return true;
        }
        return false;
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
            throw new BusinessException("otp_retries_exceeded");
        }
        createAndSendOtp(user);
    }
}
