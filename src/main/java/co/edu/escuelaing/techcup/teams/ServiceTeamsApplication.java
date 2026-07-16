package co.edu.escuelaing.techcup.teams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceTeamsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTeamsApplication.class, args);
    }
}
