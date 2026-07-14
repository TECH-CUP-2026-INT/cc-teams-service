package com.techcup.ccteams.controller;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.exception.BusinessException;
import com.techcup.ccteams.service.TeamService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamController — Unit Tests")
public class TeamControllerTest {

    @Mock
    private TeamService teamService;

    private TeamController teamController;

    @BeforeEach
    void setUp() {
        teamController = new TeamController(teamService);
    }

    // ── Get roster ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 200 with roster")
    public void testGetTeamRoster() {
        String teamId = "team-123";
        TeamRosterResponse response = new TeamRosterResponse();
        response.setTeamId(teamId);
        response.setTeamName("Los Pumas");

        when(teamService.getTeamRoster(teamId)).thenReturn(response);

        ResponseEntity<TeamRosterResponse> result = teamController.getTeamRoster(teamId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(teamId, result.getBody().getTeamId());
        verify(teamService, times(1)).getTeamRoster(teamId);
    }

    // ── Update team ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return 200 when team is updated")
    public void testUpdateTeam() {
        String teamId    = "team-123";
        String captainId = "captain-456";

        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");

        TeamUpdateResponse response = new TeamUpdateResponse();
        response.setTeamId(teamId);
        response.setName("Los Tigres");

        when(teamService.updateTeam(any(), any(), any())).thenReturn(response);

        ResponseEntity<TeamUpdateResponse> result =
                teamController.updateTeam(teamId, captainId, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Los Tigres", result.getBody().getName());
        verify(teamService, times(1)).updateTeam(any(), any(), any());
    }

    // ── TC-26: Remove player ──────────────────────────────────────────────

    @Nested
    @DisplayName("TC-26 — DELETE /api/teams/{teamId}/players/{playerId}")
    class RemovePlayerTests {

        @Test
        @DisplayName("Should return 204 when player is removed successfully")
        void shouldReturn204OnSuccess() {
            doNothing().when(teamService).removePlayer("team-123", "player-789", "captain-456");

            ResponseEntity<Void> result =
                    teamController.removePlayer("team-123", "player-789", "captain-456");

            assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
            assertNull(result.getBody());
            verify(teamService).removePlayer("team-123", "player-789", "captain-456");
        }

        @Test
        @DisplayName("Should propagate BusinessException when service throws")
        void shouldPropagateException() {
            doThrow(new BusinessException("El jugador no pertenece a este equipo"))
                    .when(teamService).removePlayer("team-123", "player-999", "captain-456");

            assertThrows(BusinessException.class,
                    () -> teamController.removePlayer("team-123", "player-999", "captain-456"));
        }
    }
}