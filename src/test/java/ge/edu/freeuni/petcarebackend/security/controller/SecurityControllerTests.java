package ge.edu.freeuni.petcarebackend.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.edu.freeuni.petcarebackend.TestUtils;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.security.controller.dto.AuthorizationTokensDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.LoginDTO;
import ge.edu.freeuni.petcarebackend.security.controller.dto.OtpDTO;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.JwtTokenService;
import ge.edu.freeuni.petcarebackend.security.service.OtpService;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import ge.edu.freeuni.petcarebackend.service.MailSenderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class SecurityControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @SpyBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private OtpService otpService;

    @SpyBean
    private SecurityService securityService;

    @MockBean
    private MailSenderService mailSenderService;


    @Test
    public void givenValidData_whenRegister_thenSuccess() throws Exception {
        UserEntity user = testUtils.createDummyUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(user)))
                .andExpect(status().isOk());

        assertTrue(testUtils.userExists(user.getUsername()));
    }

    @Test
    public void givenInvalidUsername_whenLogin_thenBusinessException() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@gmail.com", "StrongPassword!");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("invalid_credentials", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void givenInvalidPassword_whenLogin_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        LoginDTO loginDTO = new LoginDTO("test1@gmail.com", "test1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("invalid_credentials", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void givenValidCredentials_whenLogin_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        LoginDTO loginDTO = new LoginDTO(user.getUsername(), "test1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void givenExpiredRefreshToken_whenRefresh_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        AuthorizationTokensDTO authorizationTokens = jwtTokenService.generateTokens(user);
        Mockito.doReturn(true).when(jwtTokenService).isTokenExpired(authorizationTokens.refreshToken());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(authorizationTokens)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("invalid_refresh_token", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void givenNotRefreshToken_whenRefresh_thenBusinessException() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        AuthorizationTokensDTO authorizationTokens = jwtTokenService.generateTokens(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new AuthorizationTokensDTO(authorizationTokens.accessToken(), authorizationTokens.accessToken()))))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("invalid_refresh_token", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void givenValidRefreshToken_whenRefresh_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        AuthorizationTokensDTO authorizationTokens = jwtTokenService.generateTokens(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(authorizationTokens)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @WithMockUser
    @Test
    public void givenUnverifiedUserAndInvalidOTP_whenVerify_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser(false);
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setCode("123456");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(otpDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("invalid_otp", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @WithMockUser
    @Test
    public void givenUnverifiedUserAndValidOTP_whenVerify_thenSuccess() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser(false);
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
        Mockito.doReturn(true).when(otpService).verifyOtpCode(Mockito.anyString(), Mockito.any());
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setCode("123456");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(otpDTO)))
                .andExpect(status().isOk());
    }

}
