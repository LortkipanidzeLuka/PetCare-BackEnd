package ge.edu.freeuni.petcarebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.edu.freeuni.petcarebackend.TestUtils;
import ge.edu.freeuni.petcarebackend.controller.dto.EmailChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.EmailChangeOtpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PasswordChangeDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.UserDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.security.repository.OtpRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.EmailChangeOtpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.OtpService;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import ge.edu.freeuni.petcarebackend.service.MailSenderService;
import ge.edu.freeuni.petcarebackend.exception.ExceptionKeys;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private SecurityService securityService;

    @MockBean
    private MailSenderService mailSenderService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private TestUtils testUtils;

    @WithMockUser
    @Test
    public void givenAuthenticatedUser_whenGetUserInfo_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/info/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(user.getLastname()))
                .andExpect(jsonPath("$.sex").value(user.getSex().toString()))
                .andExpect(jsonPath("$.phoneNumber").value(user.getPhoneNumber()));
    }

    @WithMockUser
    @Test
    public void givenValidData_whenChangeUserInfo_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);

        UserDTO userDTO = UserDTO.builder()
                .firstname("test")
                .lastname("testishvili")
                .sex(Sex.MALE)
                .phoneNumber("511588511")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/user/info/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userDTO)))
                .andExpect(status().isOk());

        UserEntity updatedUser = testUtils.getUserById(user.getId());
        assertEquals(userDTO.getFirstname(), updatedUser.getFirstname());
        assertEquals(userDTO.getLastname(), updatedUser.getLastname());
        assertEquals(userDTO.getSex(), updatedUser.getSex());
        assertEquals(userDTO.getPhoneNumber(), updatedUser.getPhoneNumber());
    }

    @WithMockUser
    @Test
    public void givenSameEmail_whenSendEmailChangeCode_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);

        EmailChangeDTO emailChangeDTO = new EmailChangeDTO();
        emailChangeDTO.setEmail(user.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.INVALID_EMAIL, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenUsedEmail_whenSendEmailChangeCode_thenBusinessException() throws Exception {
        UserEntity user1 = testUtils.createAndPersistDummyUser("test1@gmail.com");
        UserEntity user2 = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockSecurityServiceLookupCurrentUser(user1);

        EmailChangeDTO emailChangeDTO = new EmailChangeDTO();
        emailChangeDTO.setEmail(user2.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.EMAIL_USED, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenValidState_whenSendEmailChangeCode_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        mockMailSenderService();

        EmailChangeDTO emailChangeDTO = new EmailChangeDTO();
        emailChangeDTO.setEmail("testchange@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeDTO)))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void givenOtpLimitExceeded_whenSendEmailChangeCode_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        mockMailSenderService();
        for (int i = 0; i < OtpService.OTP_RETRIES_LIMIT; i++) {
            otpService.createAndSendEmailChangeOtp(user, "estchange@gmail.com");
        }

        EmailChangeDTO emailChangeDTO = new EmailChangeDTO();
        emailChangeDTO.setEmail("testchange@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.OTP_RETRIES_EXCEEDED, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenUsedOtp_whenEmailChangeCodeVerify_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        EmailChangeOtpEntity usedOtp = createAndPersistEmailChangeOtp("test12", "testeradze@maiil.ru", LocalDateTime.now().plusMinutes(5), true, user.getId());
        EmailChangeOtpDTO emailChangeOtpDTO = createEmailChangeOtpDTO(usedOtp);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeOtpDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.INVALID_OTP, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenOutdatedOtp_whenEmailChangeCodeVerify_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        EmailChangeOtpEntity outdatedOtp = createAndPersistEmailChangeOtp("test12", "testeradze@maiil.ru", LocalDateTime.now().minusMinutes(5), false, user.getId());
        EmailChangeOtpDTO emailChangeOtpDTO = createEmailChangeOtpDTO(outdatedOtp);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeOtpDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.INVALID_OTP, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenWrongEmail_whenEmailChangeCodeVerify_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        EmailChangeOtpEntity otp = createAndPersistEmailChangeOtp("test12", "testeradze@maiil.ru", LocalDateTime.now().minusMinutes(5), false, user.getId());
        EmailChangeOtpDTO emailChangeOtpDTO = createEmailChangeOtpDTO(otp);
        emailChangeOtpDTO.setEmail("wrong@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeOtpDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.INVALID_OTP, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenUsedEmail_whenEmailChangeCodeVerify_thenBusinessException() throws Exception {
        UserEntity user1 = testUtils.createAndPersistDummyUser("test1@gmail.com");
        UserEntity user2 = testUtils.createAndPersistDummyUser("test2@gmail.com");

        mockSecurityServiceLookupCurrentUser(user1);
        EmailChangeOtpEntity otp = createAndPersistEmailChangeOtp("test12", user2.getUsername(), LocalDateTime.now().plusMinutes(5), false, user1.getId());
        EmailChangeOtpDTO emailChangeOtpDTO = createEmailChangeOtpDTO(otp);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeOtpDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.EMAIL_USED, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenValidState_whenEmailChangeCodeVerify_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        EmailChangeOtpEntity otp = createAndPersistEmailChangeOtp("test12", "test2@gmail.com", LocalDateTime.now().plusMinutes(5), false, user.getId());
        EmailChangeOtpDTO emailChangeOtpDTO = createEmailChangeOtpDTO(otp);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/email/change/code/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(emailChangeOtpDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        assertEquals("test2@gmail.com", testUtils.getUserById(user.getId()).getUsername());
    }

    @WithMockUser
    @Test
    public void givenInvalidOldPassword_whenChangePassword_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setOldPassword("test12345"); // actual is "test1234"
        passwordChangeDTO.setNewPassword("test123456");
        passwordChangeDTO.setRepeatNewPassword("test123456");

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ExceptionKeys.INVALID_OLD_PASSWORD, Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenInvalidRepeatPassword_whenChangePassword_thenBadRequest() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setOldPassword("test1234");
        passwordChangeDTO.setNewPassword("test123456");
        passwordChangeDTO.setRepeatNewPassword("test12345");

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDTO)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void givenInvalidNewPassword_whenChangePassword_thenBadRequest() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setOldPassword("test1234");
        passwordChangeDTO.setNewPassword("test1234");
        passwordChangeDTO.setRepeatNewPassword("test1234");

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDTO)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    public void givenValidData_whenChangePassword_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockSecurityServiceLookupCurrentUser(user);
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setOldPassword("test1234");
        passwordChangeDTO.setNewPassword("StrongPassword!");
        passwordChangeDTO.setRepeatNewPassword("StrongPassword!");

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(passwordChangeDTO)))
                .andExpect(status().isOk());

        assertTrue(securityService.validateUserPassword(testUtils.getUserById(user.getId()), "StrongPassword!"));
    }

    public EmailChangeOtpEntity createAndPersistEmailChangeOtp(String code, String email, LocalDateTime validTil, boolean isUsed, Long userId) {
        EmailChangeOtpEntity otpEntity = new EmailChangeOtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setUsed(isUsed);
        otpEntity.setCreateTs(LocalDateTime.now());
        otpEntity.setCode(code);
        otpEntity.setValidUntil(validTil);
        otpEntity.setUser(testUtils.getUserById(userId));
        otpRepository.saveAndFlush(otpEntity);
        return otpEntity;
    }

    public EmailChangeOtpDTO createEmailChangeOtpDTO(EmailChangeOtpEntity otp) {
        EmailChangeOtpDTO emailChangeOtpDTO = new EmailChangeOtpDTO();
        emailChangeOtpDTO.setEmail(otp.getEmail());
        emailChangeOtpDTO.setCode(otp.getCode());
        return emailChangeOtpDTO;
    }

    private void mockMailSenderService() {
        Mockito.doNothing().when(mailSenderService).sendMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    private void mockSecurityServiceLookupCurrentUser(UserEntity user) {
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
    }

}
