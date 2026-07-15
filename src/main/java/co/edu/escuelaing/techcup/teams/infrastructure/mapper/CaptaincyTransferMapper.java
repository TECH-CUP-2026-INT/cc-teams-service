package co.edu.escuelaing.techcup.teams.infrastructure.mapper;

import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.CaptaincyTransferResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.CaptaincyTransferDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaptaincyTransferMapper {

    CaptaincyTransferDocument toDocument(CaptaincyTransferRequest request);

    CaptaincyTransferRequest toDomain(CaptaincyTransferDocument document);

    CaptaincyTransferResponse toResponse(CaptaincyTransferRequest request);
}
