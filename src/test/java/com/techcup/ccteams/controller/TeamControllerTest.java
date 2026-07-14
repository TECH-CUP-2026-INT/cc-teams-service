package com.techcup.ccteams.controller;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.PlayerCardDto;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private UUID teamId;
    private UUID captainId;
    private TeamRosterResponse rosterResponse;
    private TeamUpdateResponse updateResponse;
    private TeamUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID();
        captainId = UUID.randomUUID();

        PlayerCardDto player1 = new PlayerCardDto(
            UUID.randomUUID(),
            "Jugador 1",
            "https://example.com/player1.jpg",
            "Forward",
            10,
            false
        );
        
        PlayerCardDto player2 = new PlayerCardDto(
            UUID.randomUUID(),
            "Jugador 2",
            "https://example.com/player2.jpg",
            "Goalkeeper",
            1,
            true
        );

        rosterResponse = new TeamRosterResponse(
            teamId,
            "Los Pumas",
            "https://example.com/logo.png",
            "#FF0000",
            2,
            Arrays.asList(player1, player2)
        );

        updateResponse = new TeamUpdateResponse(
            teamId,
            "Los Tigres",
            "https://example.com/new-logo.png",
            "#0000FF",
            "Equipo actualizado exitosamente"
        );

        updateRequest = new TeamUpdateRequest();
        updateRequest.setName("Los Tigres");
        updateRequest.setLogoUrl("https://example.com/new-logo.png");
        updateRequest.setColors("#0000FF");
    }

    @Test
    void getTeamRoster_ShouldReturnOk_WhenValidTeamId() {
        when(teamService.getTeamRoster(teamId)).thenReturn(rosterResponse);

        ResponseEntity<TeamRosterResponse> result = teamController.getTeamRoster(teamId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(rosterResponse, result.getBody());
        verify(teamService, times(1)).getTeamRoster(teamId);
    }

    @Test
    void updateTeam_ShouldReturnOk_WhenValidRequest() {
        when(teamService.updateTeam(any(UUID.class), any(UUID.class), any(TeamUpdateRequest.class)))
                .thenReturn(updateResponse);

        ResponseEntity<TeamUpdateResponse> result = teamController.updateTeam(teamId, captainId, updateRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updateResponse, result.getBody());
        verify(teamService, times(1)).updateTeam(any(UUID.class), any(UUID.class), any(TeamUpdateRequest.class));
    }
}