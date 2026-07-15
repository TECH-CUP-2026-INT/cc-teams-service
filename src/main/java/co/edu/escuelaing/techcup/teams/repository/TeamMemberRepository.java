package co.edu.escuelaing.techcup.teams.repository;

import co.edu.escuelaing.techcup.teams.entity.TeamMemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends MongoRepository<TeamMemberEntity, String> {
    Optional<TeamMemberEntity> findByTeamIdAndMemberEmail(String teamId, String memberEmail);
    Optional<TeamMemberEntity> findByTeamIdAndRole(String teamId, TeamMemberEntity.Role role);
}
