package com.techcup.ccteams.service;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.exception.BusinessException;
import com.techcup.ccteams.model.PlayerProfile;
import com.techcup.ccteams.model.Team;
import com.techcup.ccteams.repository.PlayerProfileRepository;
import com.techcup.ccteams.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private TeamService teamService;

    private UUID teamId;
    private UUID captainId;
    private UUID playerId1;
    private UUID playerId2;
    private Team team;
    private PlayerProfile player1;
    private PlayerProfile player2;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID();
        captainId = UUID.randomUUID();
        playerId1 = UUID.randomUUID();
        playerId2 = UUID.randomUUID();

        team = new Team();
        team.setId(teamId);
        team.setName("Los Pumas");
        team.setLogoUrl("https://example.com/logo.png");
        team.setColors("#FF0000");
        team.setCaptainId(captainId);

        player1 = new PlayerProfile();
        player1.setId(UUID.randomUUID());
        player1.setUserId(playerId1);
        player1.setTeamId(teamId);
        player1.setShirtNumber(10);
        player1.setPosition("Forward");
        player1.setPhotoUrl("https://example.com/player1.jpg");
        player1.setIsCaptain(false);
        player1.setIsActive(true);

        player2 = new PlayerProfile();
        player2.setId(UUID.randomUUID());
        player2.setUserId(playerId2);
        player2.setTeamId(teamId);
        player2.setShirtNumber(1);
        player2.setPosition("Goalkeeper");
        player2.setPhotoUrl("https://example.com/player2.jpg");
        player2.setIsCaptain(true);
        player2.setIsActive(true);
    }

    @Test
    void getTeamRoster_ShouldReturnRoster_WhenTeamExists() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerProfileRepository.findByTeamIdAndIsActiveTrue(teamId))
                .thenReturn(Arrays.asList(player1, player2));

        TeamRosterResponse response = teamService.getTeamRoster(teamId);

        assertNotNull(response);
        assertEquals(teamId, response.getTeamId());
        assertEquals("Los Pumas", response.getTeamName());
        assertEquals(2, response.getTotalPlayers());
        assertEquals(2, response.getPlayers().size());
        verify(teamRepository, times(1)).findById(teamId);
        verify(playerProfileRepository, times(1)).findByTeamIdAndIsActiveTrue(teamId);
    }

    @Test
    void getTeamRoster_ShouldThrowException_WhenTeamNotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            teamService.getTeamRoster(teamId);
        });
    }

    @Test
    void updateTeam_ShouldUpdateTeam_WhenValidData() {
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");
        request.setLogoUrl("https://example.com/new-logo.png");
        request.setColors("#0000FF");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.existsByName("Los Tigres")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamUpdateResponse response = teamService.updateTeam(teamId, captainId, request);

        assertNotNull(response);
        assertEquals(teamId, response.getTeamId());
        assertEquals("Los Tigres", response.getName());
        assertEquals("https://example.com/new-logo.png", response.getLogoUrl());
        assertEquals("#0000FF", response.getColors());
        assertEquals("Equipo actualizado exitosamente", response.getMessage());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void updateTeam_ShouldThrowException_WhenTeamNotFound() {
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            teamService.updateTeam(teamId, captainId, request);
        });
    }

    @Test
    void updateTeam_ShouldThrowException_WhenUserIsNotCaptain() {
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");
        UUID wrongCaptainId = UUID.randomUUID();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        assertThrows(BusinessException.class, () -> {
            teamService.updateTeam(teamId, wrongCaptainId, request);
        });
    }

    @Test
    void updateTeam_ShouldThrowException_WhenDuplicateTeamName() {
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.existsByName("Los Tigres")).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            teamService.updateTeam(teamId, captainId, request);
        });
    }

    @Test
    void updateTeam_ShouldOnlyUpdateLogoAndColors_WhenNameNotProvided() {
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setLogoUrl("https://example.com/new-logo.png");
        request.setColors("#0000FF");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamUpdateResponse response = teamService.updateTeam(teamId, captainId, request);

        assertNotNull(response);
        assertEquals("Los Pumas", response.getName()); // Nombre no cambia
        assertEquals("https://example.com/new-logo.png", response.getLogoUrl());
        assertEquals("#0000FF", response.getColors());
        verify(teamRepository, never()).existsByName(anyString());
    }
}