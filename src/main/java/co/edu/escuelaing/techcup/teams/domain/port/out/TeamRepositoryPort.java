package co.edu.escuelaing.techcup.teams.domain.port.out;

import co.edu.escuelaing.techcup.teams.domain.model.Team;

import java.util.Optional;

public interface TeamRepositoryPort {

    Team save(Team team);

    Optional<Team> findById(String id);

    Optional<Team> findByName(String name);

    Optional<Team> findByCaptainId(String captainId);

    Optional<Team> findByMembersUserId(String userId);

    boolean existsByName(String name);
}
