package co.edu.escuelaing.techcup.teams.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an invitation sent by a captain to a player (by email) to join a team.
 * Transitions: PENDING → ACCEPTED or PENDING → REJECTED.
 */
@Entity
@Table(name = "invitations")
public class InvitationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "invited_email", nullable = false, length = 255)
    private String invitedEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status = InvitationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public InvitationEntity() {}

    public InvitationEntity(UUID teamId, String invitedEmail) {
        this.teamId = teamId;
        this.invitedEmail = invitedEmail;
        this.status = InvitationStatus.PENDING;
    }

    public UUID getId()                 { return id; }
    public UUID getTeamId()             { return teamId; }
    public String getInvitedEmail()     { return invitedEmail; }
    public InvitationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(InvitationStatus status) { this.status = status; }
}
