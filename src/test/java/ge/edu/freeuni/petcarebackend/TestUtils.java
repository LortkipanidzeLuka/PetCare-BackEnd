package ge.edu.freeuni.petcarebackend;

import ge.edu.freeuni.petcarebackend.security.repository.UserRepository;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class TestUtils {


    private final UserRepository userRepository;

    public TestUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createAndPersistDummyUser() {
        return createAndPersistDummyUser("test@gmail.com", true);
    }

    public UserEntity createAndPersistDummyUser(String username) {
        return createAndPersistDummyUser(username, true);
    }

    public UserEntity createAndPersistDummyUser(boolean verified) {
        return createAndPersistDummyUser("test@gmail.com", false);
    }

    public UserEntity createAndPersistDummyUser(String username, boolean isVerified) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode("test1234"));
        user.setFirstname("test");
        user.setLastname("test");
        user.setSex(Sex.MALE);
        user.setVerified(isVerified);
        user.setPhoneNumber("523123123");
        userRepository.saveAndFlush(user);
        return user;
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserEntity createDummyUser() {
        UserEntity user = new UserEntity();
        user.setUsername("test@mail.com");
        user.setSex(Sex.MALE);
        user.setFirstname("Johnny");
        user.setLastname("Depp");
        user.setPhoneNumber("551581051");
        user.setPassword("StrongPassword!");
        return user;
    }

}
