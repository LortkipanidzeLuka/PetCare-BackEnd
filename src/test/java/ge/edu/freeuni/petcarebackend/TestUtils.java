package ge.edu.freeuni.petcarebackend;

import ge.edu.freeuni.petcarebackend.security.repository.EmailChangeOtpRepository;
import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.stereotype.Service;


@Service
public class TestUtils {


    private final UserRepository userRepository;


    public TestUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createAndPersistDummyUser() {
        return createAndPersistDummyUser("test@gmail.com");
    }

    public UserEntity createAndPersistDummyUser(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("test1234");
        user.setFirstname("test");
        user.setLastname("test");
        user.setSex(Sex.MALE);
        user.setVerified(true);
        user.setPhoneNumber("523123123");
        userRepository.saveAndFlush(user);
        return user;
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

}
