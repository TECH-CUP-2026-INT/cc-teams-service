package co.edu.escuelaing.techcup.teams.infrastructure.mapper;

import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamInvitationResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamInvitationDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamInvitationMapper {

    TeamInvitationDocument toDocument(TeamInvitation invitation);

    TeamInvitation toDomain(TeamInvitationDocument document);

    TeamInvitationResponse toResponse(TeamInvitation invitation);
}
