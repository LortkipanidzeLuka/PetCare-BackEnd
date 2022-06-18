package ge.edu.freeuni.petcarebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.edu.freeuni.petcarebackend.TestUtils;
import ge.edu.freeuni.petcarebackend.controller.dto.AdoptionDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AdoptionRepository;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdoptionControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private AdoptionRepository repository;

    public static final String ADOPTION_ENDPOINT = "/adoptions/";
    public static final String SEARCH_ENDPOINT = ADOPTION_ENDPOINT + "search/";

    @Test
    public void givenEmptyTable_whenSearch_thenEmptyResult() throws Exception {
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);
        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
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
        createAndPersistDummyAdoptionAdvertisement(creatorUser);
        createAndPersistDummyAdoptionAdvertisement(creatorUser);
        createAndPersistDummyAdoptionAdvertisement(creatorUser);

        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.totalCount").value("2"));
    }

    @Test
    @WithMockUser
    public void givenValidAdoptionAdvertisement_whenCreate_thenSuccess() throws Exception {
        AdoptionDTO adoption = createDummyAdoptionAdvertisementDTO();
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(ADOPTION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adoption)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void givenInvalidAdoptionAdvertisementImages_whenCreate_thenBusinessException() throws Exception {
        AdoptionDTO adoption = createDummyAdoptionAdvertisementDTO();
        adoption.setImages(Collections.emptyList());
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(ADOPTION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adoption)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("need_one_primary_image", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenUpdate_thenBusinessException() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        AdoptionEntity adoptionEntity = createAndPersistDummyAdoptionAdvertisement(creatorUser);
        AdoptionDTO adoptionDto = createDummyAdoptionAdvertisementDTO();
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.put(ADOPTION_ENDPOINT + adoptionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adoptionDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenUpdate_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        AdoptionEntity adoptionEntity = createAndPersistDummyAdoptionAdvertisement(creatorUser);
        AdoptionDTO adoptionDto = createDummyAdoptionAdvertisementDTO();
        mockCurrentUserLookup(adoptionEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.put(ADOPTION_ENDPOINT + adoptionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adoptionDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenDelete_thenDoNothing() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        AdoptionEntity adoptionEntity = createAndPersistDummyAdoptionAdvertisement(creatorUser);
        AdoptionDTO adoptionDto = createDummyAdoptionAdvertisementDTO();
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(ADOPTION_ENDPOINT + adoptionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adoptionDto)))
                .andExpect(status().isOk());
        assertTrue(repository.existsById(adoptionEntity.getId()));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenDelete_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        AdoptionEntity adoptionEntity = createAndPersistDummyAdoptionAdvertisement(creatorUser);
        AdoptionDTO adoptionDto = createDummyAdoptionAdvertisementDTO();
        mockCurrentUserLookup(adoptionEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.delete(ADOPTION_ENDPOINT + adoptionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(adoptionDto)))
                .andExpect(status().isOk());
        assertFalse(repository.existsById(adoptionEntity.getId()));
    }

    private void mockCurrentUserLookup(UserEntity user) {
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
    }

    private AdoptionDTO createDummyAdoptionAdvertisementDTO() {
        return AdoptionDTO.builder()
                .header("test")
                .city(City.RUSTAVI)
                .petType(PetType.DOG)
                .color(Color.BLACK)
                .sex(Sex.MALE)
                .description("test")
                .images(Collections.singletonList(new AdvertisementImageDTO("image.png", "test", true)))
                .build();
    }

    private AdoptionEntity createAndPersistDummyAdoptionAdvertisement(UserEntity creatorUser) {
        AdoptionEntity adoptionEntity = new AdoptionEntity();
        adoptionEntity.setAdvertisementType(AdvertisementType.ADOPTION);
        adoptionEntity.setHeader("test header");
        adoptionEntity.setCity(City.TBILISI);
        adoptionEntity.setDescription("test description");
        adoptionEntity.setColor(Color.BLACK);
        adoptionEntity.setPetType(PetType.DOG);
        adoptionEntity.setSex(Sex.MALE);
        adoptionEntity.setCreatorUser(creatorUser);
        adoptionEntity.setImages(Collections.singletonList(createAdvertisementImageEntity(adoptionEntity, true)));
        return repository.saveAndFlush(adoptionEntity);
    }

    private AdvertisementImageEntity createAdvertisementImageEntity(AdoptionEntity adoptionEntity, boolean isPrimary) {
        AdvertisementImageEntity primaryImage = new AdvertisementImageEntity();
        primaryImage.setIsPrimary(isPrimary);
        primaryImage.setTitle("test.png");
        primaryImage.setContent("test");
        primaryImage.setAdvertisement(adoptionEntity);
        return primaryImage;
    }

}
