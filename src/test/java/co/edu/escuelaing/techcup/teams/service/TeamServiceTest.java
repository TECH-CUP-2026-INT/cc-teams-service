package co.edu.escuelaing.techcup.teams.service;

import co.edu.escuelaing.techcup.teams.dto.ApiResponse;
import co.edu.escuelaing.techcup.teams.dto.TransferCaptainRequest;
import co.edu.escuelaing.techcup.teams.entity.TeamEntity;
import co.edu.escuelaing.techcup.teams.entity.TeamMemberEntity;
import co.edu.escuelaing.techcup.teams.repository.TeamMemberRepository;
import co.edu.escuelaing.techcup.teams.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamService teamService;

    private TeamEntity team;
    private TeamMemberEntity captainMember;
    private TeamMemberEntity playerMember;
    private TransferCaptainRequest request;

    private static final String CAPTAIN_EMAIL = "captain@test.com";
    private static final String PLAYER_EMAIL = "player@test.com";
    private static final Long TEAM_ID = 1L;

    @BeforeEach
    void setUp() {
        team = new TeamEntity();
        team.setName("Test Team");
        team.setCaptainEmail(CAPTAIN_EMAIL);
        team.setActive(true);

        captainMember = new TeamMemberEntity();
        captainMember.setMemberEmail(CAPTAIN_EMAIL);
        captainMember.setRole(TeamMemberEntity.Role.CAPTAIN);
        captainMember.setActive(true);
        captainMember.setTeam(team);

        playerMember = new TeamMemberEntity();
        playerMember.setMemberEmail(PLAYER_EMAIL);
        playerMember.setRole(TeamMemberEntity.Role.PLAYER);
        playerMember.setActive(true);
        playerMember.setTeam(team);

        request = new TransferCaptainRequest();
        request.setNewCaptainEmail(PLAYER_EMAIL);
    }

    @Test
    void transferCaptaincy_success() {
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamAndMemberEmail(team, PLAYER_EMAIL)).thenReturn(Optional.of(playerMember));
        when(teamMemberRepository.findByTeamAndMemberEmail(team, CAPTAIN_EMAIL)).thenReturn(Optional.of(captainMember));

        ApiResponse response = teamService.transferCaptaincy(TEAM_ID, CAPTAIN_EMAIL, request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(TeamMemberEntity.Role.PLAYER, captainMember.getRole());
        assertEquals(TeamMemberEntity.Role.CAPTAIN, playerMember.getRole());
        assertEquals(PLAYER_EMAIL, team.getCaptainEmail());
    }

    @Test
    void transferCaptaincy_teamNotFound() {
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy(TEAM_ID, CAPTAIN_EMAIL, request));

        assertEquals("Team not found or inactive", ex.getMessage());
    }

    @Test
    void transferCaptaincy_notCaptain() {
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.of(team));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy(TEAM_ID, PLAYER_EMAIL, request));

        assertEquals("Only the current captain can transfer captaincy", ex.getMessage());
    }

    @Test
    void transferCaptaincy_transferToYourself() {
        request.setNewCaptainEmail(CAPTAIN_EMAIL);
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.of(team));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy(TEAM_ID, CAPTAIN_EMAIL, request));

        assertEquals("You are already the captain", ex.getMessage());
    }

    @Test
    void transferCaptaincy_newCaptainNotMember() {
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamAndMemberEmail(team, PLAYER_EMAIL)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy(TEAM_ID, CAPTAIN_EMAIL, request));

        assertEquals("The new captain must be an active member of the team", ex.getMessage());
    }

    @Test
    void transferCaptaincy_newCaptainInactive() {
        playerMember.setActive(false);
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamAndMemberEmail(team, PLAYER_EMAIL)).thenReturn(Optional.of(playerMember));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy(TEAM_ID, CAPTAIN_EMAIL, request));

        assertEquals("The new captain must be an active member of the team", ex.getMessage());
    }

    @Test
    void transferCaptaincy_currentCaptainMemberRecordNotFound() {
        when(teamRepository.findByIdAndActiveTrue(TEAM_ID)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamAndMemberEmail(team, PLAYER_EMAIL)).thenReturn(Optional.of(playerMember));
        when(teamMemberRepository.findByTeamAndMemberEmail(team, CAPTAIN_EMAIL)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy(TEAM_ID, CAPTAIN_EMAIL, request));

        assertEquals("Current captain member record not found", ex.getMessage());
    }
}
