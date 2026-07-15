package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TeamMongoRepository extends MongoRepository<TeamDocument, String> {

    Optional<TeamDocument> findByName(String name);

    Optional<TeamDocument> findByCaptainId(String captainId);

    Optional<TeamDocument> findByMembersUserId(String userId);

    boolean existsByName(String name);
}
