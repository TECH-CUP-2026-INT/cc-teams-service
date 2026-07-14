package co.edu.escuelaing.techcup.teams.repository;

import co.edu.escuelaing.techcup.teams.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Data access layer for {@link TeamMemberEntity}.
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {

    List<TeamMemberEntity> findByTeamId(UUID teamId);

    boolean existsByTeamIdAndMemberEmail(UUID teamId, String memberEmail);

    void deleteByTeamId(UUID teamId);
}
