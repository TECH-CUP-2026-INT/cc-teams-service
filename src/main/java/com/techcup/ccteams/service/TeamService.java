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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    
    private final TeamRepository teamRepository;
    private final PlayerProfileRepository playerProfileRepository;
    
    // SCRUM-30: Consultar plantilla del equipo
    public TeamRosterResponse getTeamRoster(UUID teamId) {
        log.info("Consultando plantilla del equipo: {}", teamId);
        
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));
        
        List<PlayerProfile> players = playerProfileRepository.findByTeamIdAndIsActiveTrue(teamId);
        
        List<PlayerCardDto> playerCards = players.stream()
                .map(p -> new PlayerCardDto(
                    p.getUserId(),
                    "Jugador " + p.getUserId().toString().substring(0, 8),
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
    
    // SCRUM-27: Actualizar equipo
    @Transactional
    public TeamUpdateResponse updateTeam(UUID teamId, UUID captainId, TeamUpdateRequest request) {
        log.info("Actualizando equipo: {} por capitán: {}", teamId, captainId);
        
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("Equipo no encontrado"));
        
        // Validar que el usuario sea el capitán del equipo
        if (team.getCaptainId() == null || !team.getCaptainId().equals(captainId)) {
            throw new BusinessException("Solo el capitán puede actualizar el equipo");
        }
        
        // Validar que el nombre sea único
        if (request.getName() != null && !request.getName().equals(team.getName())) {
            if (teamRepository.existsByName(request.getName())) {
                throw new BusinessException("Ya existe un equipo con ese nombre");
            }
            team.setName(request.getName());
        }
        
        if (request.getLogoUrl() != null) {
            team.setLogoUrl(request.getLogoUrl());
        }
        
        if (request.getColors() != null) {
            team.setColors(request.getColors());
        }
        
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
}