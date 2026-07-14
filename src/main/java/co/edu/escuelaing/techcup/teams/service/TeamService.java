package co.edu.escuelaing.techcup.teams.service;

import co.edu.escuelaing.techcup.teams.dto.*;
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

import java.util.List;
import java.util.UUID;

/**
 * Core business logic for team management.
 * Handles creation, lineup consultation and team deletion.
 * The authenticated user is identified by their email (from JWT subject).
 */
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InvitationRepository invitationRepository;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       InvitationRepository invitationRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.invitationRepository = invitationRepository;
    }

    /**
     * Creates a new team. The authenticated captain is automatically added as the first member.
     *
     * @param request      team creation data
     * @param captainEmail email of the authenticated captain
     * @return the created team as a DTO
     * @throws BusinessException if the team name is already taken
     */
    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request, String captainEmail) {
        if (teamRepository.existsByName(request.getName())) {
            throw new BusinessException("A team with that name already exists.");
        }

        TeamEntity team = TeamEntity.builder()
                .name(request.getName())
                .logo(request.getLogo())
                .captainEmail(captainEmail)
                .build();

        teamRepository.save(team);
        teamMemberRepository.save(new TeamMemberEntity(team.getId(), captainEmail));

        return TeamResponse.from(team);
    }

    /**
     * Returns the lineup of a team.
     * Only the captain or a member of the team may view the lineup.
     *
     * @param teamId        UUID of the team
     * @param requesterEmail email of the authenticated user
     * @return lineup DTO with all members
     * @throws ResourceNotFoundException if the team does not exist
     * @throws ForbiddenException        if the requester is not the captain or a member
     */
    @Transactional(readOnly = true)
    public LineupResponse getLineup(UUID teamId, String requesterEmail) {
        TeamEntity team = findTeamById(teamId);

        boolean isCaptain = team.getCaptainEmail().equals(requesterEmail);
        boolean isMember  = teamMemberRepository.existsByTeamIdAndMemberEmail(teamId, requesterEmail);

        if (!isCaptain && !isMember) {
            throw new ForbiddenException("You are not a member of this team.");
        }

        List<MemberResponse> members = teamMemberRepository.findByTeamId(teamId).stream()
                .map(m -> new MemberResponse(m.getMemberEmail(), m.getJoinedAt()))
                .toList();

        return new LineupResponse(team.getId(), team.getName(), team.getCaptainEmail(), members);
    }

    /**
     * Deletes a team and all its related members and invitations.
     * Not allowed if the team is currently participating in an active tournament.
     *
     * @param teamId       UUID of the team
     * @param captainEmail email of the authenticated user (must be current captain)
     * @throws ForbiddenException if the caller is not the captain
     * @throws BusinessException  if the team is in an active tournament
     */
    @Transactional
    public void deleteTeam(UUID teamId, String captainEmail) {
        TeamEntity team = findTeamById(teamId);

        if (!team.getCaptainEmail().equals(captainEmail)) {
            throw new ForbiddenException("Only the captain can delete the team.");
        }

        if (team.isInActiveTournament()) {
            throw new BusinessException("Cannot delete a team that is participating in an active tournament.");
        }

        invitationRepository.deleteByTeamId(teamId);
        teamMemberRepository.deleteByTeamId(teamId);
        teamRepository.delete(team);
    }

    private TeamEntity findTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found."));
    }
}
