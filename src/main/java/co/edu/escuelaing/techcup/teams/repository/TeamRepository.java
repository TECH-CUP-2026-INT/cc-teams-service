package co.edu.escuelaing.techcup.teams.repository;

import co.edu.escuelaing.techcup.teams.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data access layer for {@link TeamEntity}.
 */
@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {

    boolean existsByName(String name);
}
