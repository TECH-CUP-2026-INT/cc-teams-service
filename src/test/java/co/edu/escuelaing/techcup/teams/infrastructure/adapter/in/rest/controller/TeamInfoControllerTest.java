package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamInfoUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TeamInfoController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TeamInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetTeamInfoUseCase getTeamInfoUseCase;

    private final UUID teamId = UUID.randomUUID();

    @Test
    void getTeamInfoReturns200() throws Exception {
        when(getTeamInfoUseCase.getTeamInfo(teamId)).thenReturn(new GetTeamInfoUseCase.TeamInfo("Los Halcones FC", 7));

        mockMvc.perform(get("/teams/" + teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value("Los Halcones FC"))
                .andExpect(jsonPath("$.rosterSize").value(7));
    }

    @Test
    void getTeamInfoReturns404WhenTeamMissing() throws Exception {
        when(getTeamInfoUseCase.getTeamInfo(teamId)).thenThrow(new TeamNotFoundException(teamId));

        mockMvc.perform(get("/teams/" + teamId))
                .andExpect(status().isNotFound());
    }
}
