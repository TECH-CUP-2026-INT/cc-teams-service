package co.edu.escuelaing.techcup.teams.dto;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for the team lineup (list of members).
 */
public class LineupResponse {

    private UUID teamId;
    private String teamName;
    private String captainEmail;
    private List<MemberResponse> members;

    public LineupResponse(UUID teamId, String teamName, String captainEmail, List<MemberResponse> members) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.captainEmail = captainEmail;
        this.members = members;
    }

    public UUID getTeamId()                  { return teamId; }
    public String getTeamName()              { return teamName; }
    public String getCaptainEmail()          { return captainEmail; }
    public List<MemberResponse> getMembers() { return members; }
}
