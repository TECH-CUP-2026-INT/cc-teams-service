package com.techcup.ccteams.repository;

import com.techcup.ccteams.model.PlayerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerProfileRepository extends JpaRepository<PlayerProfile, UUID> {
    List<PlayerProfile> findByTeamIdAndIsActiveTrue(UUID teamId);
    Optional<PlayerProfile> findByUserId(UUID userId);
    Optional<PlayerProfile> findByTeamIdAndIsCaptainTrue(UUID teamId);
    boolean existsByTeamIdAndShirtNumber(UUID teamId, Integer shirtNumber);
}