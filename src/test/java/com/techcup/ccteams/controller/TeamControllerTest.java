package com.techcup.ccteams.controller;
import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {
    @Mock
    private TeamService teamService;
    @InjectMocks
    private TeamController teamController;
    @Test
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
    @Test
    public void testUpdateTeam() {
        String teamId = "team-123";
        String captainId = "captain-456";
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");
        TeamUpdateResponse response = new TeamUpdateResponse();
        response.setTeamId(teamId);
        response.setName("Los Tigres");
        when(teamService.updateTeam(any(String.class), any(String.class), any(TeamUpdateRequest.class)))
            .thenReturn(response);
        ResponseEntity<TeamUpdateResponse> result = teamController.updateTeam(teamId, captainId, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Los Tigres", result.getBody().getName());
        verify(teamService, times(1)).updateTeam(any(String.class), any(String.class), any(TeamUpdateRequest.class));
    }
}