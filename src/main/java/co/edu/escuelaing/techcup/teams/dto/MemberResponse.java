package co.edu.escuelaing.techcup.teams.dto;

import java.time.LocalDateTime;

/**
 * Represents a single member entry in the team lineup.
 */
public class MemberResponse {

    private String memberEmail;
    private LocalDateTime joinedAt;

    public MemberResponse(String memberEmail, LocalDateTime joinedAt) {
        this.memberEmail = memberEmail;
        this.joinedAt = joinedAt;
    }

    public String getMemberEmail()      { return memberEmail; }
    public LocalDateTime getJoinedAt()  { return joinedAt; }
}
