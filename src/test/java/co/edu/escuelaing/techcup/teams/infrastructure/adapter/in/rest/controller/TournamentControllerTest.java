package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.port.in.ConsultTournamentTeamsUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.in.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TournamentController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultTournamentTeamsUseCase consultTournamentTeamsUseCase;
    @MockBean
    private EnrollTeamInTournamentUseCase enrollTeamInTournamentUseCase;

    private final UUID captainId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();
    private final UUID tournamentId = UUID.randomUUID();

    private Principal principalFor(UUID userId) {
        return new UsernamePasswordAuthenticationToken(userId, "token", List.of());
    }

    @Test
    void getRegisteredTeamsReturns200() throws Exception {
        when(consultTournamentTeamsUseCase.getRegisteredTeams(tournamentId)).thenReturn(
                List.of(new TournamentServicePort.TournamentTeam(teamId, "Los Halcones FC", "APPROVED", "https://logo")));

        mockMvc.perform(get("/api/v1/tournaments/" + tournamentId + "/teams")
                        .principal(principalFor(captainId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamName").value("Los Halcones FC"));
    }

    @Test
    void enrollTeamReturns201() throws Exception {
        when(enrollTeamInTournamentUseCase.enrollTeam(eq(captainId), eq(teamId), eq(tournamentId)))
                .thenReturn(new TournamentServicePort.Enrollment(UUID.randomUUID(), "RESERVED", null));

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/teams/" + teamId + "/enrollment")
                        .principal(principalFor(captainId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RESERVED"));
    }
}
