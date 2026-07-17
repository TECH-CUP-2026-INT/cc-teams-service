package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamInvitationRepositoryPort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamInvitationDocument;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.TeamInvitationMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamInvitationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamInvitationRepositoryAdapter implements TeamInvitationRepositoryPort {

    private final TeamInvitationMongoRepository mongoRepository;
    private final TeamInvitationMapper mapper;

    @Override
    public TeamInvitation save(TeamInvitation invitation) {
        TeamInvitationDocument document = mapper.toDocument(invitation);
        TeamInvitationDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TeamInvitation> findById(UUID id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TeamInvitation> findByInvitedUserId(UUID userId) {
        return mongoRepository.findByInvitedUserId(userId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<TeamInvitation> findByTeamId(UUID teamId) {
        return mongoRepository.findByTeamId(teamId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TeamInvitation> findByTeamIdAndInvitedUserIdAndStatus(UUID teamId, UUID userId, InvitationStatus status) {
        return mongoRepository.findByTeamIdAndInvitedUserIdAndStatus(teamId, userId, status)
                .map(mapper::toDomain);
    }
}
