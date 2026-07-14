package com.techcup.ccteams.repository;
import com.techcup.ccteams.model.PlayerProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface PlayerProfileRepository extends MongoRepository<PlayerProfile, String> {
    List<PlayerProfile> findByTeamIdAndIsActiveTrue(String teamId);
    Optional<PlayerProfile> findByUserId(String userId);
    Optional<PlayerProfile> findByTeamIdAndIsCaptainTrue(String teamId);
    boolean existsByTeamIdAndShirtNumber(String teamId, Integer shirtNumber);
}