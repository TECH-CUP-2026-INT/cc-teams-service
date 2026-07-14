package co.edu.escuelaing.techcup.teams.service;

import co.edu.escuelaing.techcup.teams.dto.InvitationResponse;
import co.edu.escuelaing.techcup.teams.dto.InvitePlayerRequest;
import co.edu.escuelaing.techcup.teams.dto.RespondInvitationRequest;
import co.edu.escuelaing.techcup.teams.entity.InvitationEntity;
import co.edu.escuelaing.techcup.teams.entity.InvitationStatus;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InvitationServiceTest {

    private InvitationRepository invitationRepository;
    private TeamRepository teamRepository;
    private TeamMemberRepository teamMemberRepository;
    private InvitationService invitationService;

    private final String captainEmail = "captain@test.com";
    private final String invitedEmail = "player@test.com";
    private final UUID teamId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        invitationRepository = mock(InvitationRepository.class);
        teamRepository       = mock(TeamRepository.class);
        teamMemberRepository = mock(TeamMemberRepository.class);
        invitationService = new InvitationService(invitationRepository, teamRepository, teamMemberRepository);
    }

    // ── sendInvitation ───────────────────────────────────────────────────────

    @Test
    void sendInvitation_success() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(buildTeam(captainEmail)));
        when(teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, invitedEmail)).thenReturn(false);
        when(invitationRepository.existsByTeamIdAndInvitedEmailAndStatus(
                teamId, invitedEmail, InvitationStatus.PENDING)).thenReturn(false);
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvitePlayerRequest request = new InvitePlayerRequest();
        request.setInvitedEmail(invitedEmail);

        InvitationResponse response = invitationService.sendInvitation(teamId, request, captainEmail);

        assertEquals(teamId, response.getTeamId());
        assertEquals(invitedEmail, response.getInvitedEmail());
        assertEquals(InvitationStatus.PENDING, response.getStatus());
    }

    @Test
    void sendInvitation_notCaptain_throwsForbidden() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(buildTeam(captainEmail)));

        InvitePlayerRequest request = new InvitePlayerRequest();
        request.setInvitedEmail(invitedEmail);

        assertThrows(ForbiddenException.class,
                () -> invitationService.sendInvitation(teamId, request, "other@test.com"));
    }

    @Test
    void sendInvitation_alreadyMember_throwsBusiness() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(buildTeam(captainEmail)));
        when(teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, invitedEmail)).thenReturn(true);

        InvitePlayerRequest request = new InvitePlayerRequest();
        request.setInvitedEmail(invitedEmail);

        assertThrows(BusinessException.class,
                () -> invitationService.sendInvitation(teamId, request, captainEmail));
    }

    @Test
    void sendInvitation_pendingAlreadyExists_throwsBusiness() {
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(buildTeam(captainEmail)));
        when(teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, invitedEmail)).thenReturn(false);
        when(invitationRepository.existsByTeamIdAndInvitedEmailAndStatus(
                teamId, invitedEmail, InvitationStatus.PENDING)).thenReturn(true);

        InvitePlayerRequest request = new InvitePlayerRequest();
        request.setInvitedEmail(invitedEmail);

        assertThrows(BusinessException.class,
                () -> invitationService.sendInvitation(teamId, request, captainEmail));
    }

    // ── respondToInvitation ──────────────────────────────────────────────────

    @Test
    void respondToInvitation_accept_addsMember() {
        UUID invitationId = UUID.randomUUID();
        InvitationEntity invitation = new InvitationEntity(teamId, invitedEmail);
        when(invitationRepository.findByIdAndInvitedEmail(invitationId, invitedEmail))
                .thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setStatus(InvitationStatus.ACCEPTED);

        InvitationResponse response = invitationService.respondToInvitation(invitationId, request, invitedEmail);

        assertEquals(InvitationStatus.ACCEPTED, response.getStatus());
        verify(teamMemberRepository).save(any(TeamMemberEntity.class));
    }

    @Test
    void respondToInvitation_reject_doesNotAddMember() {
        UUID invitationId = UUID.randomUUID();
        InvitationEntity invitation = new InvitationEntity(teamId, invitedEmail);
        when(invitationRepository.findByIdAndInvitedEmail(invitationId, invitedEmail))
                .thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setStatus(InvitationStatus.REJECTED);

        InvitationResponse response = invitationService.respondToInvitation(invitationId, request, invitedEmail);

        assertEquals(InvitationStatus.REJECTED, response.getStatus());
        verify(teamMemberRepository, never()).save(any());
    }

    @Test
    void respondToInvitation_alreadyResponded_throwsBusiness() {
        UUID invitationId = UUID.randomUUID();
        InvitationEntity invitation = new InvitationEntity(teamId, invitedEmail);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        when(invitationRepository.findByIdAndInvitedEmail(invitationId, invitedEmail))
                .thenReturn(Optional.of(invitation));

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setStatus(InvitationStatus.REJECTED);

        assertThrows(BusinessException.class,
                () -> invitationService.respondToInvitation(invitationId, request, invitedEmail));
    }

    @Test
    void respondToInvitation_invalidStatus_throwsBusiness() {
        UUID invitationId = UUID.randomUUID();
        InvitationEntity invitation = new InvitationEntity(teamId, invitedEmail);
        when(invitationRepository.findByIdAndInvitedEmail(invitationId, invitedEmail))
                .thenReturn(Optional.of(invitation));

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setStatus(InvitationStatus.PENDING);

        assertThrows(BusinessException.class,
                () -> invitationService.respondToInvitation(invitationId, request, invitedEmail));
    }

    @Test
    void respondToInvitation_notFound_throwsResourceNotFound() {
        UUID invitationId = UUID.randomUUID();
        when(invitationRepository.findByIdAndInvitedEmail(invitationId, invitedEmail))
                .thenReturn(Optional.empty());

        RespondInvitationRequest request = new RespondInvitationRequest();
        request.setStatus(InvitationStatus.ACCEPTED);

        assertThrows(ResourceNotFoundException.class,
                () -> invitationService.respondToInvitation(invitationId, request, invitedEmail));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private TeamEntity buildTeam(String captain) {
        return TeamEntity.builder()
                .name("Test Team")
                .logo("logo.png")
                .captainEmail(captain)
                .build();
    }
}
