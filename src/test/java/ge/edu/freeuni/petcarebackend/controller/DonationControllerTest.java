package ge.edu.freeuni.petcarebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.edu.freeuni.petcarebackend.TestUtils;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.DonationRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import ge.edu.freeuni.petcarebackend.security.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DonationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private DonationRepository repository;

    public static final String DONATION_ENDPOINT = "/advertisements/donations/";
    public static final String SEARCH_ENDPOINT = DONATION_ENDPOINT + "search/";

    public static final String SEARCH_NEED_DONATION_ENDPOINT = SEARCH_ENDPOINT + "NEED_DONATION";

    @Test
    public void givenEmptyTable_whenSearch_thenEmptyResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_NEED_DONATION_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalCount").value("0"));
    }

    @Test
    public void givenFilledTable_whenSearch_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(creatorUser);
        createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.NEED_DONATION, creatorUser);
        createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.ABLE_TO_DONATE, creatorUser);
        createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.NEED_DONATION, creatorUser);

        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_NEED_DONATION_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.totalCount").value("2"));
    }

    @Test
    @WithMockUser
    public void givenValidDonationAdvertisement_whenCreate_thenSuccess() throws Exception {
        DonationDTO donation = createDummyDonationAdvertisementDTO(DonationAdvertisementType.NEED_DONATION);
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(DONATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(donation)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void givenInvalidDonationAdvertisementImages_whenCreate_thenBusinessException() throws Exception {
        DonationDTO donation = createDummyDonationAdvertisementDTO(DonationAdvertisementType.ABLE_TO_DONATE);
        donation.setImages(Collections.emptyList());
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(DONATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(donation)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("need_one_primary_image", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenUpdate_thenBusinessException() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        DonationEntity donationEntity = createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.NEED_DONATION, creatorUser);
        DonationDTO donationDto = createDummyDonationAdvertisementDTO(DonationAdvertisementType.NEED_DONATION);
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.put(DONATION_ENDPOINT + donationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(donationDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenUpdate_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        DonationEntity donationEntity = createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.NEED_DONATION, creatorUser);
        DonationDTO donationDto = createDummyDonationAdvertisementDTO(DonationAdvertisementType.NEED_DONATION);
        mockCurrentUserLookup(donationEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.put(DONATION_ENDPOINT + donationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(donationDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenDelete_thenDoNothing() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        DonationEntity donationEntity = createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.NEED_DONATION, creatorUser);
        DonationDTO donationDto = createDummyDonationAdvertisementDTO(DonationAdvertisementType.NEED_DONATION);
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(DONATION_ENDPOINT + donationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(donationDto)))
                .andExpect(status().isOk());
        assertTrue(repository.existsById(donationEntity.getId()));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenDelete_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        DonationEntity donationEntity = createAndPersistDummyDonationAdvertisement(DonationAdvertisementType.NEED_DONATION, creatorUser);
        DonationDTO donationDto = createDummyDonationAdvertisementDTO(DonationAdvertisementType.NEED_DONATION);
        mockCurrentUserLookup(donationEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.delete(DONATION_ENDPOINT + donationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(donationDto)))
                .andExpect(status().isOk());
        assertFalse(repository.existsById(donationEntity.getId()));
    }

    private void mockCurrentUserLookup(UserEntity user) {
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
    }

    private DonationDTO createDummyDonationAdvertisementDTO(DonationAdvertisementType type) {
        return DonationDTO.builder()
                .header("test")
                .city(City.RUSTAVI)
                .description("test")
                .applicablePetList(List.of(PetType.DOG))
                .donationAdvertisementType(type)
                .images(Collections.singletonList(new AdvertisementImageDTO("image.png", "test", true)))
                .build();
    }

    private DonationEntity createAndPersistDummyDonationAdvertisement(DonationAdvertisementType type, UserEntity creatorUser) {
        DonationEntity donationEntity = new DonationEntity();
        donationEntity.setAdvertisementType(AdvertisementType.DONATION);
        donationEntity.setHeader("test header");
        donationEntity.setCity(City.TBILISI);
        donationEntity.setDescription("test description");
        donationEntity.setDonationAdvertisementType(type);
        donationEntity.setCreatorUser(creatorUser);
        donationEntity.setImages(Collections.singletonList(createAdvertisementImageEntity(donationEntity, true)));
        return repository.saveAndFlush(donationEntity);
    }

    private AdvertisementImageEntity createAdvertisementImageEntity(DonationEntity donationEntity, boolean isPrimary) {
        AdvertisementImageEntity primaryImage = new AdvertisementImageEntity();
        primaryImage.setIsPrimary(isPrimary);
        primaryImage.setTitle("test.png");
        primaryImage.setContent("test");
        primaryImage.setAdvertisement(donationEntity);
        return primaryImage;
    }

}
