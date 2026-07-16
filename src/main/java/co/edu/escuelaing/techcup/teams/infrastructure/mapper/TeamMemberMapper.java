package co.edu.escuelaing.techcup.teams.infrastructure.mapper;

import co.edu.escuelaing.techcup.teams.domain.model.TeamMember;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamMemberDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    TeamMemberDocument toDocument(TeamMember member);

    TeamMember toDomain(TeamMemberDocument document);
}
