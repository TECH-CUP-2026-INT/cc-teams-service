package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamInvitationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeamInvitationMongoRepository extends MongoRepository<TeamInvitationDocument, String> {

    List<TeamInvitationDocument> findByInvitedUserId(String userId);

    List<TeamInvitationDocument> findByTeamId(String teamId);

    Optional<TeamInvitationDocument> findByTeamIdAndInvitedUserIdAndStatus(String teamId, String userId, InvitationStatus status);
}
