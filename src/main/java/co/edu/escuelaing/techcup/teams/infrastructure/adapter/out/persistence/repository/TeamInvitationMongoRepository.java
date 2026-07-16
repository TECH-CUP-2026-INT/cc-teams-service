package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamInvitationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamInvitationMongoRepository extends MongoRepository<TeamInvitationDocument, UUID> {

    List<TeamInvitationDocument> findByInvitedUserId(UUID userId);

    List<TeamInvitationDocument> findByTeamId(UUID teamId);

    Optional<TeamInvitationDocument> findByTeamIdAndInvitedUserIdAndStatus(UUID teamId, UUID userId, InvitationStatus status);
}
