package co.edu.escuelaing.techcup.teams.service;

import co.edu.escuelaing.techcup.teams.dto.ApiResponse;
import co.edu.escuelaing.techcup.teams.dto.TransferCaptainRequest;
import co.edu.escuelaing.techcup.teams.entity.TeamEntity;
import co.edu.escuelaing.techcup.teams.entity.TeamMemberEntity;
import co.edu.escuelaing.techcup.teams.repository.TeamMemberRepository;
import co.edu.escuelaing.techcup.teams.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for team operations.
 * Covers SCRUM-22: captaincy transfer.
 */
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * Transfers captaincy from the current captain to another team member.
     * Only the current captain can perform this action (SCRUM-22).
     *
     * @param teamId          the team ID
     * @param currentCaptainEmail email of the user making the request (from JWT)
     * @param request         contains the email of the new captain
     * @return confirmation message
     */
    @Transactional
    public ApiResponse transferCaptaincy(Long teamId, String currentCaptainEmail,
                                         TransferCaptainRequest request) {

        // 1. Verify team exists and is active
        TeamEntity team = teamRepository.findByIdAndActiveTrue(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found or inactive"));

        // 2. Verify the requester is the current captain
        if (!team.getCaptainEmail().equals(currentCaptainEmail)) {
            throw new RuntimeException("Only the current captain can transfer captaincy");
        }

        // 3. Cannot transfer to yourself
        if (currentCaptainEmail.equals(request.getNewCaptainEmail())) {
            throw new RuntimeException("You are already the captain");
        }

        // 4. Verify new captain is an active member of the team
        TeamMemberEntity newCaptainMember = teamMemberRepository
                .findByTeamAndMemberEmail(team, request.getNewCaptainEmail())
                .orElseThrow(() -> new RuntimeException("The new captain must be an active member of the team"));

        if (!newCaptainMember.isActive()) {
            throw new RuntimeException("The new captain must be an active member of the team");
        }

        // 5. Demote current captain to PLAYER
        TeamMemberEntity currentCaptainMember = teamMemberRepository
                .findByTeamAndMemberEmail(team, currentCaptainEmail)
                .orElseThrow(() -> new RuntimeException("Current captain member record not found"));

        currentCaptainMember.setRole(TeamMemberEntity.Role.PLAYER);
        teamMemberRepository.save(currentCaptainMember);

        // 6. Promote new captain
        newCaptainMember.setRole(TeamMemberEntity.Role.CAPTAIN);
        teamMemberRepository.save(newCaptainMember);

        // 7. Update captain email on the team
        team.setCaptainEmail(request.getNewCaptainEmail());
        teamRepository.save(team);

        return new ApiResponse("Captaincy successfully transferred to " + request.getNewCaptainEmail(), true);
    }
}
