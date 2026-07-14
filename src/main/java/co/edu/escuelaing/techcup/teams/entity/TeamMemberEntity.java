package co.edu.escuelaing.techcup.teams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents the membership of a user in a team.
 * Stores the user's email (from cc-identity-service), the team they belong to,
 * and their role within the team (CAPTAIN or PLAYER).
 */
@Entity
@Table(name = "team_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "member_email"}))
public class TeamMemberEntity {

    public enum Role {
        CAPTAIN,
        PLAYER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Column(name = "member_email", nullable = false)
    private String memberEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
        this.active = true;
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TeamEntity team;
        private String memberEmail;
        private Role role;

        public Builder team(TeamEntity team) { this.team = team; return this; }
        public Builder memberEmail(String memberEmail) { this.memberEmail = memberEmail; return this; }
        public Builder role(Role role) { this.role = role; return this; }

        public TeamMemberEntity build() {
            TeamMemberEntity e = new TeamMemberEntity();
            e.team = this.team;
            e.memberEmail = this.memberEmail;
            e.role = this.role;
            return e;
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    public TeamEntity getTeam() { return team; }
    public String getMemberEmail() { return memberEmail; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getJoinedAt() { return joinedAt; }

    public void setId(Long id) { this.id = id; }
    public void setTeam(TeamEntity team) { this.team = team; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
