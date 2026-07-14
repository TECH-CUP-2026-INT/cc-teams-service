package co.edu.escuelaing.techcup.teams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a team in the TechCup platform.
 * A team is created by a Captain and can participate in tournaments.
 */
@Entity
@Table(name = "teams")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String logoUrl;

    @Column(nullable = false)
    private String captainEmail;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String logoUrl;
        private String captainEmail;

        public Builder name(String name) { this.name = name; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder captainEmail(String captainEmail) { this.captainEmail = captainEmail; return this; }

        public TeamEntity build() {
            TeamEntity e = new TeamEntity();
            e.name = this.name;
            e.logoUrl = this.logoUrl;
            e.captainEmail = this.captainEmail;
            return e;
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLogoUrl() { return logoUrl; }
    public String getCaptainEmail() { return captainEmail; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setCaptainEmail(String captainEmail) { this.captainEmail = captainEmail; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
