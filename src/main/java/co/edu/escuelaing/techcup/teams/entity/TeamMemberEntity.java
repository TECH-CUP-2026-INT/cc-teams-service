package co.edu.escuelaing.techcup.teams.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the membership of a user (identified by email) in a team.
 */
@Entity
@Table(name = "team_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "member_email"}))
public class TeamMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "member_email", nullable = false, length = 255)
    private String memberEmail;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    public TeamMemberEntity() {}

    public TeamMemberEntity(UUID teamId, String memberEmail) {
        this.teamId = teamId;
        this.memberEmail = memberEmail;
    }

    public UUID getId()               { return id; }
    public UUID getTeamId()           { return teamId; }
    public String getMemberEmail()    { return memberEmail; }
    public LocalDateTime getJoinedAt(){ return joinedAt; }
}
