package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.domain.port.out.CaptaincyTransferRepositoryPort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.CaptaincyTransferMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.CaptaincyTransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CaptaincyTransferRepositoryAdapter implements CaptaincyTransferRepositoryPort {

    private final CaptaincyTransferMongoRepository mongoRepository;
    private final CaptaincyTransferMapper mapper;

    @Override
    public CaptaincyTransferRequest save(CaptaincyTransferRequest request) {
        var document = mapper.toDocument(request);
        var saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CaptaincyTransferRequest> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CaptaincyTransferRequest> findByTeamIdAndStatus(String teamId, TransferRequestStatus status) {
        return mongoRepository.findByTeamIdAndStatus(teamId, status).map(mapper::toDomain);
    }
}
