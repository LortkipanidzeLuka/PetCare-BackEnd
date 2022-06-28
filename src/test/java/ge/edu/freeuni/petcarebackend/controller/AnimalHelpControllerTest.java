package ge.edu.freeuni.petcarebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.edu.freeuni.petcarebackend.TestUtils;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementImageDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.LostFoundDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.AnimalHelpRepository;
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
class AnimalHelpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private AnimalHelpRepository repository;


    public static final String ANIMAL_HELP_ENDPOINT = "/advertisements/animalhelp/";
    public static final String ANIMAL_HELP_SEARCH_ENDPOINT = ANIMAL_HELP_ENDPOINT + "search/LOST";

    @Test
    public void givenEmptyTable_whenSearch_thenEmptyResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_HELP_SEARCH_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.totalCount").value("0"));
    }

    @Test
    public void givenFilledTable_whenSearch_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();

        createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.LOST, creatorUser);
        createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.FOUND, creatorUser);
        createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.LOST, creatorUser);

        mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_HELP_SEARCH_ENDPOINT)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.totalCount").value("2"));
    }

    @Test
    @WithMockUser
    public void givenValidLostAdvertisement_whenCreate_thenSuccess() throws Exception {
        LostFoundDTO lostFound = createDummyLostFoundAdvertisementDTO(AnimalHelpType.LOST);
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(ANIMAL_HELP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lostFound)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void givenInvalidLostAdvertisementImages_whenCreate_thenBusinessException() throws Exception {
        LostFoundDTO lostFound = createDummyLostFoundAdvertisementDTO(AnimalHelpType.FOUND);
        lostFound.setImages(Collections.emptyList());
        UserEntity user = testUtils.createAndPersistDummyUser();
        mockCurrentUserLookup(user);

        mockMvc.perform(MockMvcRequestBuilders.post(ANIMAL_HELP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lostFound)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals("need_one_primary_image", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenUpdate_thenBusinessException() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        AnimalHelpEntity animalHelpEntity = createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.LOST, creatorUser);
        LostFoundDTO lostFoundDTO = createDummyLostFoundAdvertisementDTO(AnimalHelpType.LOST);
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.put(ANIMAL_HELP_ENDPOINT + animalHelpEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lostFoundDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenUpdate_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        AnimalHelpEntity animalHelpEntity = createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.LOST, creatorUser);
        LostFoundDTO lostFoundDTO = createDummyLostFoundAdvertisementDTO(AnimalHelpType.LOST);
        mockCurrentUserLookup(animalHelpEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.put(ANIMAL_HELP_ENDPOINT + animalHelpEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lostFoundDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void givenInvalidAdvertisementIdAndUser_whenDelete_thenDoNothing() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser("test1@gmail.com");
        AnimalHelpEntity animalHelpEntity = createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.LOST, creatorUser);
        LostFoundDTO lostFoundDTO = createDummyLostFoundAdvertisementDTO(AnimalHelpType.LOST);
        UserEntity invalidUser = testUtils.createAndPersistDummyUser("test2@gmail.com");
        mockCurrentUserLookup(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(ANIMAL_HELP_ENDPOINT + animalHelpEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lostFoundDTO)))
                .andExpect(status().isOk());
        assertTrue(repository.existsById(animalHelpEntity.getId()));
    }

    @Test
    @WithMockUser
    public void givenValidData_whenDelete_thenSuccess() throws Exception {
        UserEntity creatorUser = testUtils.createAndPersistDummyUser();
        AnimalHelpEntity animalHelpEntity = createAndPersistDummyLostFoundAdvertisement(AnimalHelpType.LOST, creatorUser);
        LostFoundDTO lostFoundDTO = createDummyLostFoundAdvertisementDTO(AnimalHelpType.LOST);
        mockCurrentUserLookup(animalHelpEntity.getCreatorUser());

        mockMvc.perform(MockMvcRequestBuilders.delete(ANIMAL_HELP_ENDPOINT + animalHelpEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(lostFoundDTO)))
                .andExpect(status().isOk());
        assertFalse(repository.existsById(animalHelpEntity.getId()));
    }

    private void mockCurrentUserLookup(UserEntity user) {
        Mockito.doReturn(user).when(securityService).lookupCurrentUser();
    }

    private LostFoundDTO createDummyLostFoundAdvertisementDTO(AnimalHelpType type) {
        return LostFoundDTO.builder()
                .header("test")
                .city(City.RUSTAVI)
                .description("test")
                .petType(PetType.DOG)
                .type(type)
                .sex(Sex.MALE)
                .images(Collections.singletonList(new AdvertisementImageDTO("image.png", "test", true)))
                .build();
    }

    private AnimalHelpEntity createAndPersistDummyLostFoundAdvertisement(AnimalHelpType type, UserEntity creatorUser) {
        AnimalHelpEntity animalHelpEntity = new AnimalHelpEntity();
        animalHelpEntity.setAdvertisementType(AdvertisementType.LOST_FOUND);
        animalHelpEntity.setHeader("test header");
        animalHelpEntity.setCity(City.TBILISI);
        animalHelpEntity.setDescription("test description");
        animalHelpEntity.setPetType(PetType.CAT);
        animalHelpEntity.setType(type);
        animalHelpEntity.setSex(Sex.MALE);
        animalHelpEntity.setCreatorUser(creatorUser);
        animalHelpEntity.setImages(Collections.singletonList(createAdvertisementImageEntity(animalHelpEntity, true)));
        return repository.saveAndFlush(animalHelpEntity);
    }

    private AdvertisementImageEntity createAdvertisementImageEntity(AnimalHelpEntity animalHelpEntity, boolean isPrimary) {
        AdvertisementImageEntity primaryImage = new AdvertisementImageEntity();
        primaryImage.setIsPrimary(isPrimary);
        primaryImage.setTitle("test.png");
        primaryImage.setContent("test");
        primaryImage.setAdvertisement(animalHelpEntity);
        return primaryImage;
    }

}
