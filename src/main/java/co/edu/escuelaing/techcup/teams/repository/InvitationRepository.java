package co.edu.escuelaing.techcup.teams.repository;

import co.edu.escuelaing.techcup.teams.entity.InvitationEntity;
import co.edu.escuelaing.techcup.teams.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data access layer for {@link InvitationEntity}.
 */
@Repository
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {

    boolean existsByTeamIdAndInvitedEmailAndStatus(UUID teamId, String invitedEmail, InvitationStatus status);

    void deleteByTeamId(UUID teamId);

    Optional<InvitationEntity> findByIdAndInvitedEmail(UUID id, String invitedEmail);
}
