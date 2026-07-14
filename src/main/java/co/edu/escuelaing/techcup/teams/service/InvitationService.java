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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Business logic for managing team invitations.
 * Handles sending invitations and processing player responses.
 * Users are identified by their email (from JWT subject).
 */
@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public InvitationService(InvitationRepository invitationRepository,
                             TeamRepository teamRepository,
                             TeamMemberRepository teamMemberRepository) {
        this.invitationRepository = invitationRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * Sends an invitation from the captain to a player.
     *
     * @param teamId       UUID of the team
     * @param request      contains the invited player's email
     * @param captainEmail email of the authenticated user (must be captain)
     * @return the created invitation as a DTO
     * @throws ForbiddenException if the caller is not the team captain
     * @throws BusinessException  if the player is already a member or has a pending invitation
     */
    @Transactional
    public InvitationResponse sendInvitation(UUID teamId, InvitePlayerRequest request, String captainEmail) {
        TeamEntity team = findTeamById(teamId);

        if (!team.getCaptainEmail().equals(captainEmail)) {
            throw new ForbiddenException("Only the captain can send invitations.");
        }

        String invitedEmail = request.getInvitedEmail();

        if (teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, invitedEmail)) {
            throw new BusinessException("This player is already a member of the team.");
        }

        if (invitationRepository.existsByTeamIdAndInvitedEmailAndStatus(
                teamId, invitedEmail, InvitationStatus.PENDING)) {
            throw new BusinessException("This player already has a pending invitation.");
        }

        InvitationEntity invitation = new InvitationEntity(teamId, invitedEmail);
        invitationRepository.save(invitation);

        return InvitationResponse.from(invitation);
    }

    /**
     * Allows the invited player to accept or reject an invitation.
     * If accepted, the player is added as a team member.
     *
     * @param invitationId UUID of the invitation
     * @param request      contains the player's response (ACCEPTED or REJECTED)
     * @param userEmail    email of the authenticated user (must be the invited player)
     * @return the updated invitation as a DTO
     * @throws ResourceNotFoundException if the invitation does not exist or does not belong to the user
     * @throws BusinessException         if the invitation is not PENDING or status is invalid
     */
    @Transactional
    public InvitationResponse respondToInvitation(UUID invitationId,
                                                   RespondInvitationRequest request,
                                                   String userEmail) {
        InvitationEntity invitation = invitationRepository
                .findByIdAndInvitedEmail(invitationId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found."));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BusinessException("This invitation has already been responded to.");
        }

        InvitationStatus newStatus = request.getStatus();
        if (newStatus == InvitationStatus.PENDING) {
            throw new BusinessException("Invalid response status. Use ACCEPTED or REJECTED.");
        }

        invitation.setStatus(newStatus);
        invitationRepository.save(invitation);

        if (newStatus == InvitationStatus.ACCEPTED) {
            teamMemberRepository.save(new TeamMemberEntity(invitation.getTeamId(), userEmail));
        }

        return InvitationResponse.from(invitation);
    }

    private TeamEntity findTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found."));
    }
}
