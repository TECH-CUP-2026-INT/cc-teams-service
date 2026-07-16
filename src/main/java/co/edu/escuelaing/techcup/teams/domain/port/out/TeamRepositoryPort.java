package co.edu.escuelaing.techcup.teams.domain.port.out;

import co.edu.escuelaing.techcup.teams.domain.model.Team;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepositoryPort {

    Team save(Team team);

    Optional<Team> findById(UUID id);

    Optional<Team> findByName(String name);

    Optional<Team> findByCaptainId(UUID captainId);

    Optional<Team> findByMembersUserId(UUID userId);

    boolean existsByName(String name);
}
