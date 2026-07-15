package co.edu.escuelaing.techcup.teams.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents the membership of a user in a team.
 * Stores the user's email (from cc-identity-service), the team ID they belong to,
 * and their role within the team (CAPTAIN or PLAYER).
 */
@Document(collection = "team_members")
public class TeamMemberEntity {

    public enum Role {
        CAPTAIN,
        PLAYER
    }

    @Id
    private String id;

    private String teamId;

    private String memberEmail;

    private Role role;

    private boolean active;

    @CreatedDate
    private LocalDateTime joinedAt;

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String teamId;
        private String memberEmail;
        private Role role;

        public Builder teamId(String teamId) { this.teamId = teamId; return this; }
        public Builder memberEmail(String memberEmail) { this.memberEmail = memberEmail; return this; }
        public Builder role(Role role) { this.role = role; return this; }

        public TeamMemberEntity build() {
            TeamMemberEntity e = new TeamMemberEntity();
            e.teamId = this.teamId;
            e.memberEmail = this.memberEmail;
            e.role = this.role;
            e.active = true;
            return e;
        }
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTeamId() { return teamId; }
    public String getMemberEmail() { return memberEmail; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setId(String id) { this.id = id; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
