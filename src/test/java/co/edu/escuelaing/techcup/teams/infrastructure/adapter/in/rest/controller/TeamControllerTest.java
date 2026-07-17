package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.in.CreateTeamUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler.GlobalExceptionHandler;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TeamController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateTeamUseCase createTeamUseCase;
    @MockBean
    private TeamMapper teamMapper;

    @Test
    void createTeamReturns201() throws Exception {
        UUID captainId = UUID.randomUUID();
        var principal = new UsernamePasswordAuthenticationToken(captainId, "Bearer some-jwt-token", List.of());

        Team team = Team.builder().id(UUID.randomUUID()).name("Los Halcones FC").captainId(captainId).build();
        when(createTeamUseCase.createTeam(any(), any(), any(), any(), any(), any())).thenReturn(team);
        when(teamMapper.toResponse(team)).thenReturn(
                TeamResponse.builder().id(team.getId()).name(team.getName()).captainId(captainId).memberCount(1).build());

        MockMultipartFile teamPart = new MockMultipartFile("team", "team", "application/json",
                "{\"name\":\"Los Halcones FC\",\"colors\":\"#FF0000\",\"captainName\":\"Ada Lovelace\"}".getBytes());
        MockMultipartFile logoPart = new MockMultipartFile("logo", "logo.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/v1/teams")
                        .file(teamPart)
                        .file(logoPart)
                        .principal(principal)
                        .with(req -> { req.setMethod("POST"); return req; }))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Los Halcones FC"));
    }
}
