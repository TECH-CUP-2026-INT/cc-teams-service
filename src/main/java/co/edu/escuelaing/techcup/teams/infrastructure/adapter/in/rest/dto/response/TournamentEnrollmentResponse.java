package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentEnrollmentResponse(UUID enrollmentId, String status, LocalDateTime reservationExpiresAt) {}
