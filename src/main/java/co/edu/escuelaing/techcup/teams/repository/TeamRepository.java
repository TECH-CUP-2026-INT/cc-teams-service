package co.edu.escuelaing.techcup.teams.repository;

import co.edu.escuelaing.techcup.teams.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    boolean existsByName(String name);
    Optional<TeamEntity> findByIdAndActiveTrue(Long id);
}
