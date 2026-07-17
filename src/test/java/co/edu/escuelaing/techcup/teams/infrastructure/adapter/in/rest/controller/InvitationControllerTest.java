package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.domain.port.in.ManageInvitationUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamInvitationResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler.GlobalExceptionHandler;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamInvitationMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = InvitationController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManageInvitationUseCase manageInvitationUseCase;
    @MockBean
    private TeamInvitationMapper invitationMapper;

    private final UUID captainId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();
    private final UUID invitedUserId = UUID.randomUUID();
    private final UUID invitationId = UUID.randomUUID();

    private Principal principalFor(UUID userId) {
        return new UsernamePasswordAuthenticationToken(userId, "token", List.of());
    }

    private TeamInvitation invitation() {
        return TeamInvitation.builder()
                .id(invitationId).teamId(teamId).teamName("Los Halcones FC")
                .invitedUserId(invitedUserId).invitedBy(captainId)
                .status(InvitationStatus.PENDING).build();
    }

    private TeamInvitationResponse invitationResponse() {
        return TeamInvitationResponse.builder()
                .id(invitationId).teamId(teamId).teamName("Los Halcones FC")
                .invitedUserId(invitedUserId).invitedBy(captainId)
                .status(InvitationStatus.PENDING).build();
    }

    @Test
    void sendInvitationReturns201() throws Exception {
        when(manageInvitationUseCase.sendInvitation(eq(captainId), eq(teamId), eq(invitedUserId)))
                .thenReturn(invitation());
        when(invitationMapper.toResponse(any())).thenReturn(invitationResponse());

        mockMvc.perform(post("/api/v1/invitations/teams/" + teamId)
                        .principal(principalFor(captainId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invitedUserId\":\"" + invitedUserId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void respondToInvitationReturns200() throws Exception {
        when(manageInvitationUseCase.respondToInvitation(eq(invitedUserId), any(), eq(invitationId), eq(true)))
                .thenReturn(invitation());
        when(invitationMapper.toResponse(any())).thenReturn(invitationResponse());

        mockMvc.perform(put("/api/v1/invitations/" + invitationId + "/respond")
                        .principal(principalFor(invitedUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accept\":true,\"userName\":\"New Player\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getMyInvitationsReturnsList() throws Exception {
        when(manageInvitationUseCase.getInvitationsForUser(invitedUserId)).thenReturn(List.of(invitation()));
        when(invitationMapper.toResponse(any())).thenReturn(invitationResponse());

        mockMvc.perform(get("/api/v1/invitations/my").principal(principalFor(invitedUserId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(invitationId.toString()));
    }

    @Test
    void getTeamInvitationsReturnsList() throws Exception {
        when(manageInvitationUseCase.getInvitationsForTeam(captainId, teamId)).thenReturn(List.of(invitation()));
        when(invitationMapper.toResponse(any())).thenReturn(invitationResponse());

        mockMvc.perform(get("/api/v1/invitations/teams/" + teamId).principal(principalFor(captainId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(teamId.toString()));
    }

    @Test
    void sendInvitationRejectsMalformedUuidInBody() throws Exception {
        mockMvc.perform(post("/api/v1/invitations/teams/" + teamId)
                        .principal(principalFor(captainId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invitedUserId\":\"not-a-uuid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MALFORMED_JSON"));
    }
}
