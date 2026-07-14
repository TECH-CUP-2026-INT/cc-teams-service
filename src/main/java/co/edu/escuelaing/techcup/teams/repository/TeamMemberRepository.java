package co.edu.escuelaing.techcup.teams.repository;

import co.edu.escuelaing.techcup.teams.entity.TeamEntity;
import co.edu.escuelaing.techcup.teams.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Long> {
    Optional<TeamMemberEntity> findByTeamAndMemberEmail(TeamEntity team, String memberEmail);
    Optional<TeamMemberEntity> findByTeamAndRole(TeamEntity team, TeamMemberEntity.Role role);
}
