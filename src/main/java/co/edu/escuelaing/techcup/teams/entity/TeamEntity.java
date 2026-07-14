package co.edu.escuelaing.techcup.teams.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a team in the Tech-Cup platform.
 * A team is owned by a captain identified by their email.
 * The {@code inActiveTournament} flag prevents deletion while the team is competing.
 */
@Entity
@Table(name = "teams")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String logo;

    @Column(name = "captain_email", nullable = false, length = 255)
    private String captainEmail;

    @Column(name = "in_active_tournament", nullable = false)
    private boolean inActiveTournament = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TeamEntity() {}

    private TeamEntity(Builder builder) {
        this.name = builder.name;
        this.logo = builder.logo;
        this.captainEmail = builder.captainEmail;
        this.inActiveTournament = builder.inActiveTournament;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String logo;
        private String captainEmail;
        private boolean inActiveTournament = false;

        public Builder name(String name)                { this.name = name; return this; }
        public Builder logo(String logo)                { this.logo = logo; return this; }
        public Builder captainEmail(String email)       { this.captainEmail = email; return this; }
        public Builder inActiveTournament(boolean flag) { this.inActiveTournament = flag; return this; }
        public TeamEntity build()                       { return new TeamEntity(this); }
    }

    public UUID getId()                   { return id; }
    public String getName()               { return name; }
    public String getLogo()               { return logo; }
    public String getCaptainEmail()       { return captainEmail; }
    public boolean isInActiveTournament() { return inActiveTournament; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    public void setName(String name)                        { this.name = name; }
    public void setLogo(String logo)                        { this.logo = logo; }
    public void setCaptainEmail(String captainEmail)        { this.captainEmail = captainEmail; }
    public void setInActiveTournament(boolean flag)         { this.inActiveTournament = flag; }
}
