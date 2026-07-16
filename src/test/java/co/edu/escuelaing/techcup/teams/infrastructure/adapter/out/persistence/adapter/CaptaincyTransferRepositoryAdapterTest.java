package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.CaptaincyTransferDocument;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.CaptaincyTransferMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.CaptaincyTransferMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaptaincyTransferRepositoryAdapterTest {

    @Mock
    private CaptaincyTransferMongoRepository mongoRepository;
    @Mock
    private CaptaincyTransferMapper mapper;

    private CaptaincyTransferRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CaptaincyTransferRepositoryAdapter(mongoRepository, mapper);
    }

    @Test
    void generatesIdWhenSavingANewRequest() {
        CaptaincyTransferRequest request = CaptaincyTransferRequest.builder().teamName("Los Halcones FC").build();
        CaptaincyTransferDocument document = CaptaincyTransferDocument.builder().build();
        when(mapper.toDocument(any(CaptaincyTransferRequest.class))).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(request);

        adapter.save(request);

        assertThat(request.getId()).isNotNull();
    }

    @Test
    void keepsExistingIdWhenSavingAnAlreadyPersistedRequest() {
        UUID existingId = UUID.randomUUID();
        CaptaincyTransferRequest request = CaptaincyTransferRequest.builder().id(existingId).build();
        CaptaincyTransferDocument document = CaptaincyTransferDocument.builder().build();
        when(mapper.toDocument(request)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(request);

        adapter.save(request);

        assertThat(request.getId()).isEqualTo(existingId);
    }

    @Test
    void findByTeamIdAndStatusDelegatesToMongoRepository() {
        UUID teamId = UUID.randomUUID();
        CaptaincyTransferDocument document = CaptaincyTransferDocument.builder().teamId(teamId).build();
        CaptaincyTransferRequest request = CaptaincyTransferRequest.builder().teamId(teamId).build();
        when(mongoRepository.findByTeamIdAndStatus(teamId, TransferRequestStatus.PENDING))
                .thenReturn(Optional.of(document));
        when(mapper.toDomain(document)).thenReturn(request);

        assertThat(adapter.findByTeamIdAndStatus(teamId, TransferRequestStatus.PENDING)).contains(request);
    }
}
