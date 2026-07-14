package co.edu.escuelaing.techcup.teams;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "app.jwt.secret=test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm"
})
class ServiceTeamsApplicationTests {

    @Test
    void contextLoads() {
    }
}
