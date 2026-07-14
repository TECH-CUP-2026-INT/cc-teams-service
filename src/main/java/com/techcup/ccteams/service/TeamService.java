package com.techcup.ccteams.service;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.PlayerCardDto;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.exception.BusinessException;
import com.techcup.ccteams.model.PlayerProfile;
import com.techcup.ccteams.model.Team;
import com.techcup.ccteams.repository.PlayerProfileRepository;
import com.techcup.ccteams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerProfileRepository playerProfileRepository;

    // ── Consultar plantilla ───────────────────────────────────────────────

    public TeamRosterResponse getTeamRoster(String teamId) {
        log.info("Consultando plantilla del equipo: {}", teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        List<PlayerProfile> players = playerProfileRepository.findByTeamIdAndIsActiveTrue(teamId);

        List<PlayerCardDto> playerCards = players.stream()
                .map(p -> new PlayerCardDto(
                        p.getUserId(),
                        "Jugador " + p.getUserId().substring(0, Math.min(8, p.getUserId().length())),
                        p.getPhotoUrl() != null ? p.getPhotoUrl() : "",
                        p.getPosition() != null ? p.getPosition() : "",
                        p.getShirtNumber() != null ? p.getShirtNumber() : 0,
                        p.getIsCaptain()
                ))
                .collect(Collectors.toList());

        return new TeamRosterResponse(
                team.getId(),
                team.getName(),
                team.getLogoUrl(),
                team.getColors(),
                playerCards.size(),
                playerCards
        );
    }

    // ── Actualizar equipo ────────────────────────────────────────────────

    @Transactional
    public TeamUpdateResponse updateTeam(String teamId, String captainId, TeamUpdateRequest request) {
        log.info("Actualizando equipo: {} por capitán: {}", teamId, captainId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        if (team.getCaptainId() == null || !team.getCaptainId().equals(captainId)) {
            throw new BusinessException("Solo el capitán puede actualizar el equipo");
        }

        if (request.getName() != null && !request.getName().equals(team.getName())) {
            if (teamRepository.existsByName(request.getName())) {
                throw new BusinessException("Ya existe un equipo con ese nombre");
            }
            team.setName(request.getName());
        }

        if (request.getLogoUrl() != null) team.setLogoUrl(request.getLogoUrl());
        if (request.getColors() != null) team.setColors(request.getColors());

        team.setUpdatedAt(LocalDateTime.now());
        Team updated = teamRepository.save(team);

        log.info("Equipo actualizado: {}", updated.getId());

        return new TeamUpdateResponse(
                updated.getId(),
                updated.getName(),
                updated.getLogoUrl(),
                updated.getColors(),
                "Equipo actualizado exitosamente"
        );
    }

    // ── TC-26: Retirar jugador del equipo ────────────────────────────────

    /**
     * TC-26 — Retirar jugadores existentes del equipo.
     *
     * Solo el capitán del equipo puede retirar jugadores.
     * El capitán no puede retirarse a sí mismo.
     * El jugador debe ser miembro activo del equipo.
     * El retiro es lógico: isActive = false, no eliminación física.
     *
     * @param teamId    ID del equipo
     * @param playerId  ID del jugador a retirar (userId)
     * @param captainId ID del capitán que solicita el retiro (X-Captain-Id header)
     */
    @Transactional
    public void removePlayer(String teamId, String playerId, String captainId) {
        log.info("Retiro de jugador {} del equipo {} solicitado por capitán {}", playerId, teamId, captainId);

        // 1. Verificar que el equipo existe
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));

        // 2. Verificar que quien solicita es el capitán del equipo
        if (team.getCaptainId() == null || !team.getCaptainId().equals(captainId)) {
            throw new BusinessException("Solo el capitán puede retirar jugadores del equipo");
        }

        // 3. El capitán no puede retirarse a sí mismo
        if (captainId.equals(playerId)) {
            throw new BusinessException("El capitán no puede retirarse a sí mismo del equipo");
        }

        // 4. Verificar que el jugador es miembro activo del equipo
        PlayerProfile profile = playerProfileRepository.findByUserId(playerId)
                .filter(p -> teamId.equals(p.getTeamId()))
                .orElseThrow(() -> new BusinessException("El jugador no pertenece a este equipo"));

        if (!Boolean.TRUE.equals(profile.getIsActive())) {
            throw new BusinessException("El jugador ya fue retirado del equipo");
        }

        // 5. Retiro lógico — no se elimina el registro
        profile.setIsActive(false);
        profile.setUpdatedAt(LocalDateTime.now());
        playerProfileRepository.save(profile);

        log.info("Jugador {} retirado exitosamente del equipo {}", playerId, teamId);
    }
}