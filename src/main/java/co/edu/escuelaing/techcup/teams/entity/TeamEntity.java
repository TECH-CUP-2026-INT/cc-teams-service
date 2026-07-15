package co.edu.escuelaing.techcup.teams.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a team in the TechCup platform.
 * A team is created by a Captain and can participate in tournaments.
 */
@Document(collection = "teams")
public class TeamEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String logoUrl;

    private String captainEmail;

    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

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
            e.active = true;
            return e;
        }
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getLogoUrl() { return logoUrl; }
    public String getCaptainEmail() { return captainEmail; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setCaptainEmail(String captainEmail) { this.captainEmail = captainEmail; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
