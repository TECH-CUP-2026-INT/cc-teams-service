package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.port.in.CheckPlayerActiveTournamentUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckPlayerActiveTournamentUseCaseImpl implements CheckPlayerActiveTournamentUseCase {

    private final TeamRepositoryPort teamRepository;
    private final TournamentServicePort tournamentServicePort;

    @Override
    public boolean hasActiveTournament(UUID playerId) {
        return teamRepository.findByMembersUserId(playerId)
                .map(team -> tournamentServicePort.hasActiveEnrollment(team.getId()))
                .orElse(false);
    }
}
