package co.edu.escuelaing.techcup.teams.domain.port.in;

import java.util.UUID;

/**
 * Resuelve si un jugador está inscrito (vía su equipo) en un torneo activo o
 * en curso. Consumido por users-players-service para TC-19 (deshabilitar
 * usuario) y TC-16/TC-17 (bloquear edición de perfil).
 */
public interface CheckPlayerActiveTournamentUseCase {

    /**
     * @return {@code false} si el jugador no pertenece a ningún equipo, o si
     *         Tournament Service no está disponible / no expone el chequeo
     *         (falla abierto: nunca bloquea por una integración caída).
     */
    boolean hasActiveTournament(UUID playerId);
}
