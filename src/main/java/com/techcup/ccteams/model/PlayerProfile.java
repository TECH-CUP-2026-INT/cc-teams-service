package com.techcup.ccteams.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "player_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    
    @Column(name = "team_id")
    private UUID teamId;
    
    @Column(name = "shirt_number")
    private Integer shirtNumber;
    
    @Column(name = "position")
    private String position;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Column(name = "is_captain", nullable = false)
    private Boolean isCaptain = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}