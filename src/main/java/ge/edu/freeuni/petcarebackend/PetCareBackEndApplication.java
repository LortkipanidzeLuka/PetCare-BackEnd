package ge.edu.freeuni.petcarebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PetCareBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetCareBackEndApplication.class, args);
    }

}
