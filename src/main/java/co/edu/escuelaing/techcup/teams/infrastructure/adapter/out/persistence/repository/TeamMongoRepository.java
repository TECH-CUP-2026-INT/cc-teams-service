package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamMongoRepository extends MongoRepository<TeamDocument, UUID> {

    Optional<TeamDocument> findByName(String name);

    Optional<TeamDocument> findByCaptainId(UUID captainId);

    Optional<TeamDocument> findByMembersUserId(UUID userId);

    boolean existsByName(String name);
}
