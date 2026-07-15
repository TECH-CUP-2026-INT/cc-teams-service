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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private TeamMemberRepository teamMemberRepository;
    @InjectMocks private TeamService teamService;

    private TeamEntity team;
    private TeamMemberEntity captainMember;
    private TeamMemberEntity playerMember;

    @BeforeEach
    void setUp() {
        team = new TeamEntity();
        team.setId("team-1");
        team.setName("Los Pumas");
        team.setCaptainEmail("captain@test.com");
        team.setActive(true);

        captainMember = new TeamMemberEntity();
        captainMember.setTeamId("team-1");
        captainMember.setMemberEmail("captain@test.com");
        captainMember.setRole(TeamMemberEntity.Role.CAPTAIN);
        captainMember.setActive(true);

        playerMember = new TeamMemberEntity();
        playerMember.setTeamId("team-1");
        playerMember.setMemberEmail("player@test.com");
        playerMember.setRole(TeamMemberEntity.Role.PLAYER);
        playerMember.setActive(true);
    }

    // ── SCRUM-22: transferCaptaincy ──────────────────────────────────────────

    @Test
    void transferCaptaincy_success() {
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("player@test.com");

        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "player@test.com")).thenReturn(Optional.of(playerMember));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "captain@test.com")).thenReturn(Optional.of(captainMember));

        ApiResponse response = teamService.transferCaptaincy("team-1", "captain@test.com", request);

        assertTrue(response.isSuccess());
        assertEquals("Captaincy successfully transferred to player@test.com", response.getMessage());
        assertEquals(TeamMemberEntity.Role.PLAYER, captainMember.getRole());
        assertEquals(TeamMemberEntity.Role.CAPTAIN, playerMember.getRole());
        assertEquals("player@test.com", team.getCaptainEmail());
        verify(teamMemberRepository, times(2)).save(any());
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    void transferCaptaincy_teamNotFound_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("99")).thenReturn(Optional.empty());
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("player@test.com");
        assertThrows(RuntimeException.class, () -> teamService.transferCaptaincy("99", "captain@test.com", request));
    }

    @Test
    void transferCaptaincy_notCaptain_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("player@test.com");
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy("team-1", "impostor@test.com", request));
        assertEquals("Only the current captain can transfer captaincy", ex.getMessage());
    }

    @Test
    void transferCaptaincy_sameEmail_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("captain@test.com");
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy("team-1", "captain@test.com", request));
        assertEquals("You are already the captain", ex.getMessage());
    }

    @Test
    void transferCaptaincy_newCaptainNotMember_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "stranger@test.com")).thenReturn(Optional.empty());
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("stranger@test.com");
        assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy("team-1", "captain@test.com", request));
    }

    @Test
    void transferCaptaincy_newCaptainInactive_throwsException() {
        playerMember.setActive(false);
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "player@test.com")).thenReturn(Optional.of(playerMember));
        TransferCaptainRequest request = new TransferCaptainRequest();
        request.setNewCaptainEmail("player@test.com");
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.transferCaptaincy("team-1", "captain@test.com", request));
        assertEquals("The new captain must be an active member of the team", ex.getMessage());
    }

    // ── SCRUM-61: disableMember ──────────────────────────────────────────────

    @Test
    void disableMember_success() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "player@test.com"))
                .thenReturn(Optional.of(playerMember));

        ApiResponse response = teamService.disableMember("team-1", "captain@test.com", "player@test.com");

        assertTrue(response.isSuccess());
        assertFalse(playerMember.isActive());
        verify(teamMemberRepository, times(1)).save(playerMember);
    }

    @Test
    void disableMember_teamNotFound_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("99")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> teamService.disableMember("99", "captain@test.com", "player@test.com"));
    }

    @Test
    void disableMember_notCaptain_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.disableMember("team-1", "impostor@test.com", "player@test.com"));
        assertEquals("Only the captain can disable team members", ex.getMessage());
    }

    @Test
    void disableMember_selfDisable_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.disableMember("team-1", "captain@test.com", "captain@test.com"));
        assertEquals("The captain cannot disable themselves", ex.getMessage());
    }

    @Test
    void disableMember_memberNotFound_throwsException() {
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "unknown@test.com"))
                .thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.disableMember("team-1", "captain@test.com", "unknown@test.com"));
        assertEquals("Member not found in this team", ex.getMessage());
    }

    @Test
    void disableMember_alreadyInactive_throwsException() {
        playerMember.setActive(false);
        when(teamRepository.findByIdAndActiveTrue("team-1")).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndMemberEmail("team-1", "player@test.com"))
                .thenReturn(Optional.of(playerMember));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.disableMember("team-1", "captain@test.com", "player@test.com"));
        assertEquals("Member is already inactive", ex.getMessage());
    }
}
