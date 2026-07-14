package co.edu.escuelaing.techcup.teams.service;

import co.edu.escuelaing.techcup.teams.dto.CreateTeamRequest;
import co.edu.escuelaing.techcup.teams.dto.LineupResponse;
import co.edu.escuelaing.techcup.teams.dto.TeamResponse;
import co.edu.escuelaing.techcup.teams.entity.TeamEntity;
import co.edu.escuelaing.techcup.teams.entity.TeamMemberEntity;
import co.edu.escuelaing.techcup.teams.exception.BusinessException;
import co.edu.escuelaing.techcup.teams.exception.ForbiddenException;
import co.edu.escuelaing.techcup.teams.exception.ResourceNotFoundException;
import co.edu.escuelaing.techcup.teams.repository.InvitationRepository;
import co.edu.escuelaing.techcup.teams.repository.TeamMemberRepository;
import co.edu.escuelaing.techcup.teams.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    private TeamRepository teamRepository;
    private TeamMemberRepository teamMemberRepository;
    private InvitationRepository invitationRepository;
    private TeamService teamService;

    private final String captainEmail = "captain@test.com";
    private final UUID teamId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        teamRepository       = mock(TeamRepository.class);
        teamMemberRepository = mock(TeamMemberRepository.class);
        invitationRepository = mock(InvitationRepository.class);
        teamService = new TeamService(teamRepository, teamMemberRepository, invitationRepository);
    }

    // ── createTeam ───────────────────────────────────────────────────────────

    @Test
    void createTeam_success() {
        CreateTeamRequest request = buildCreateRequest("Dragons", "logo.png");
        when(teamRepository.existsByName("Dragons")).thenReturn(false);
        when(teamRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TeamResponse response = teamService.createTeam(request, captainEmail);

        assertEquals("Dragons", response.getName());
        assertEquals(captainEmail, response.getCaptainEmail());
        verify(teamMemberRepository).save(any(TeamMemberEntity.class));
    }

    @Test
    void createTeam_duplicateName_throwsBusinessException() {
        CreateTeamRequest request = buildCreateRequest("Dragons", "logo.png");
        when(teamRepository.existsByName("Dragons")).thenReturn(true);

        assertThrows(BusinessException.class, () -> teamService.createTeam(request, captainEmail));
        verify(teamRepository, never()).save(any());
    }

    // ── getLineup ────────────────────────────────────────────────────────────

    @Test
    void getLineup_captainCanView() {
        TeamEntity team = buildTeam(captainEmail, false);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamId(teamId)).thenReturn(List.of());

        LineupResponse lineup = teamService.getLineup(teamId, captainEmail);

        assertNotNull(lineup);
        assertEquals(captainEmail, lineup.getCaptainEmail());
    }

    @Test
    void getLineup_memberCanView() {
        String memberEmail = "member@test.com";
        TeamEntity team = buildTeam(captainEmail, false);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, memberEmail)).thenReturn(true);
        when(teamMemberRepository.findByTeamId(teamId)).thenReturn(List.of());

        assertDoesNotThrow(() -> teamService.getLineup(teamId, memberEmail));
    }

    @Test
    void getLineup_outsider_throwsForbidden() {
        String outsider = "outsider@test.com";
        TeamEntity team = buildTeam(captainEmail, false);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, outsider)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> teamService.getLineup(teamId, outsider));
    }

    @Test
    void getLineup_teamNotFound_throwsResourceNotFound() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> teamService.getLineup(teamId, captainEmail));
    }

    // ── deleteTeam ───────────────────────────────────────────────────────────

    @Test
    void deleteTeam_success() {
        TeamEntity team = buildTeam(captainEmail, false);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        assertDoesNotThrow(() -> teamService.deleteTeam(teamId, captainEmail));

        verify(invitationRepository).deleteByTeamId(teamId);
        verify(teamMemberRepository).deleteByTeamId(teamId);
        verify(teamRepository).delete(team);
    }

    @Test
    void deleteTeam_notCaptain_throwsForbidden() {
        TeamEntity team = buildTeam(captainEmail, false);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        assertThrows(ForbiddenException.class,
                () -> teamService.deleteTeam(teamId, "other@test.com"));
        verify(teamRepository, never()).delete(any());
    }

    @Test
    void deleteTeam_inActiveTournament_throwsBusiness() {
        TeamEntity team = buildTeam(captainEmail, true);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        assertThrows(BusinessException.class, () -> teamService.deleteTeam(teamId, captainEmail));
        verify(teamRepository, never()).delete(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private CreateTeamRequest buildCreateRequest(String name, String logo) {
        CreateTeamRequest r = new CreateTeamRequest();
        r.setName(name);
        r.setLogo(logo);
        return r;
    }

    private TeamEntity buildTeam(String captain, boolean inTournament) {
        return TeamEntity.builder()
                .name("Test Team")
                .logo("logo.png")
                .captainEmail(captain)
                .inActiveTournament(inTournament)
                .build();
    }
}
