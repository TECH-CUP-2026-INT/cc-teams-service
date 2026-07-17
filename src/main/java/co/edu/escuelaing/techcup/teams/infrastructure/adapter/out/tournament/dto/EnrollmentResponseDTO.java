package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentResponseDTO(UUID enrollmentId, String status, LocalDateTime reservationExpiresAt) {}
