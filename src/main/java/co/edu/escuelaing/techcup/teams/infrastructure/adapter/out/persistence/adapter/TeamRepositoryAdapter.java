package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.TeamMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamRepositoryAdapter implements TeamRepositoryPort {

    private final TeamMongoRepository mongoRepository;
    private final TeamMapper mapper;

    @Override
    public Team save(Team team) {
        if (team.getId() == null) {
            team.setId(UUID.randomUUID());
        }
        var document = mapper.toDocument(team);
        var saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Team> findById(UUID id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Team> findByName(String name) {
        return mongoRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public Optional<Team> findByCaptainId(UUID captainId) {
        return mongoRepository.findByCaptainId(captainId).map(mapper::toDomain);
    }

    @Override
    public Optional<Team> findByMembersUserId(UUID userId) {
        return mongoRepository.findByMembersUserId(userId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return mongoRepository.existsByName(name);
    }
}
