package ge.edu.freeuni.petcarebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.edu.freeuni.petcarebackend.TestUtils;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.PetServiceRepository;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
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
public class PetServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private PetServiceRepository repository;

    public static final String PET_SERVICE_ENDPOINT = "/advertisements/petservice/";
    public static final String SEARCH_ENDPOINT = PET_SERVICE_ENDPOINT + "search/";

    public static final String SEARCH_PET_WATCH_ENDPOINT = SEARCH_ENDPOINT + "PET_WATCH";

    @Test
    public void givenEmptyTable_whenSearch_thenEmptyResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_PET_WATCH_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalCount").value("0"));
    }

    @Test
    public void givenFilledTable_whenSearch_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        createAndPersistDummyPetServiceAdvertisement(PetServiceType.PET_WATCH, creatorUser);
        createAndPersistDummyPetServiceAdvertisement(PetServiceType.PET_WATCH, creatorUser);
        createAndPersistDummyPetServiceAdvertisement(PetServiceType.GROOMING, creatorUser);

        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_PET_WATCH_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.totalCount").value("2"));
    }

    @Test
    @WithMockUser
    public void givenValidPetServiceAdvertisement_whenCreate_thenSuccess() throws Exception {
        PetServiceDTO petService = createDummyPetServiceAdvertisementDTO(PetServiceType.PET_WATCH);
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(PET_SERVICE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petService)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void givenInvalidPetServiceAdvertisementImages_whenCreate_thenBusinessException() throws Exception {
        PetServiceDTO petService = createDummyPetServiceAdvertisementDTO(PetServiceType.GROOMING);
        petService.setImages(Collections.emptyList());
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(PET_SERVICE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petService)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("need_one_primary_image", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenUpdate_thenBusinessException() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        PetServiceEntity petServiceEntity = createAndPersistDummyPetServiceAdvertisement(PetServiceType.PET_WATCH, creatorUser);
        PetServiceDTO petServiceDto = createDummyPetServiceAdvertisementDTO(PetServiceType.PET_WATCH);
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.put(PET_SERVICE_ENDPOINT + petServiceEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petServiceDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenUpdate_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        PetServiceEntity petServiceEntity = createAndPersistDummyPetServiceAdvertisement(PetServiceType.PET_WATCH, creatorUser);
        PetServiceDTO petServiceDto = createDummyPetServiceAdvertisementDTO(PetServiceType.PET_WATCH);
        mockCurrentUserLookup(petServiceEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.put(PET_SERVICE_ENDPOINT + petServiceEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petServiceDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenDelete_thenDoNothing() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        PetServiceEntity petServiceEntity = createAndPersistDummyPetServiceAdvertisement(PetServiceType.PET_WATCH, creatorUser);
        PetServiceDTO petServiceDto = createDummyPetServiceAdvertisementDTO(PetServiceType.PET_WATCH);
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(PET_SERVICE_ENDPOINT + petServiceEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petServiceDto)))
                .andExpect(status().isOk());
        assertTrue(repository.existsById(petServiceEntity.getId()));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenDelete_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        PetServiceEntity petServiceEntity = createAndPersistDummyPetServiceAdvertisement(PetServiceType.PET_WATCH, creatorUser);
        PetServiceDTO petServiceDto = createDummyPetServiceAdvertisementDTO(PetServiceType.PET_WATCH);
        mockCurrentUserLookup(petServiceEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.delete(PET_SERVICE_ENDPOINT + petServiceEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(petServiceDto)))
                .andExpect(status().isOk());
        assertFalse(repository.existsById(petServiceEntity.getId()));
    }

    private void mockCurrentUserLookup(UserEntity user) {
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
    }

    private PetServiceDTO createDummyPetServiceAdvertisementDTO(PetServiceType type) {
        return PetServiceDTO.builder()
                .header("test")
                .city(City.RUSTAVI)
                .description("test")
                .applicablePetList(List.of(PetType.DOG))
                .petServiceType(type)
                .applicableSex(Sex.MALE)
                .images(Collections.singletonList(new AdvertisementImageDTO("image.png", "test", true)))
                .build();
    }

    private PetServiceEntity createAndPersistDummyPetServiceAdvertisement(PetServiceType type, UserEntity creatorUser) {
        PetServiceEntity petServiceEntity = new PetServiceEntity();
        petServiceEntity.setAdvertisementType(AdvertisementType.PET_SERVICE);
        petServiceEntity.setHeader("test header");
        petServiceEntity.setCity(City.TBILISI);
        petServiceEntity.setDescription("test description");
        petServiceEntity.setPetServiceType(type);
        petServiceEntity.setApplicableSex(Sex.MALE);
        petServiceEntity.setCreatorUser(creatorUser);
        petServiceEntity.setImages(Collections.singletonList(createAdvertisementImageEntity(petServiceEntity, true)));
        return repository.saveAndFlush(petServiceEntity);
    }

    private AdvertisementImageEntity createAdvertisementImageEntity(PetServiceEntity petServiceEntity, boolean isPrimary) {
        AdvertisementImageEntity primaryImage = new AdvertisementImageEntity();
        primaryImage.setIsPrimary(isPrimary);
        primaryImage.setTitle("test.png");
        primaryImage.setContent("test");
        primaryImage.setAdvertisement(petServiceEntity);
        return primaryImage;
    }

}
