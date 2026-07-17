package co.edu.escuelaing.techcup.teams.infrastructure.mapper;

import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamDocument;
import org.bson.types.Binary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = TeamMemberMapper.class)
public interface TeamMapper {

    @Mapping(target = "logo", source = "logo", qualifiedByName = "bytesToBinary")
    TeamDocument toDocument(Team team);

    @Mapping(target = "logo", source = "logo", qualifiedByName = "binaryToBytes")
    Team toDomain(TeamDocument document);

    @Mapping(target = "memberCount", expression = "java(team.getMembers() != null ? team.getMembers().size() : 0)")
    TeamResponse toResponse(Team team);

    @Named("bytesToBinary")
    default Binary bytesToBinary(byte[] bytes) {
        return bytes != null ? new Binary(bytes) : null;
    }

    @Named("binaryToBytes")
    default byte[] binaryToBytes(Binary binary) {
        return binary != null ? binary.getData() : null;
    }
}
