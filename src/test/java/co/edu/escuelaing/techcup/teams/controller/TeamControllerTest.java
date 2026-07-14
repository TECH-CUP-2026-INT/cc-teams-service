package co.edu.escuelaing.techcup.teams.controller;

import co.edu.escuelaing.techcup.teams.dto.ApiResponse;
import co.edu.escuelaing.techcup.teams.dto.TransferCaptainRequest;
import co.edu.escuelaing.techcup.teams.service.JwtService;
import co.edu.escuelaing.techcup.teams.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
@Import(TeamControllerTest.TestSecurityConfig.class)
class TeamControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) ->
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/teams/**").authenticated()
                    .anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private JwtService jwtService;
    @MockBean private TeamService teamService;

    @Test
    @WithMockUser(username = "captain@test.com")
    void transferCaptaincy_success() throws Exception {
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("player@test.com");

        when(teamService.transferCaptaincy(eq(1L), eq("captain@test.com"), any()))
                .thenReturn(new ApiResponse("Captaincy successfully transferred to player@test.com", true));

        mockMvc.perform(patch("/api/teams/1/captain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Captaincy successfully transferred to player@test.com"));
    }

    @Test
    @WithMockUser(username = "captain@test.com")
    void transferCaptaincy_invalidRequest_missingEmail() throws Exception {
        TransferCaptainRequest request = new TransferCaptainRequest();

        mockMvc.perform(patch("/api/teams/1/captain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferCaptaincy_unauthenticated() throws Exception {
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("player@test.com");

        mockMvc.perform(patch("/api/teams/1/captain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
