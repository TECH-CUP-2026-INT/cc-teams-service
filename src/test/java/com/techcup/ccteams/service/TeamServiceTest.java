package com.techcup.ccteams.service;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.exception.BusinessException;
import com.techcup.ccteams.model.PlayerProfile;
import com.techcup.ccteams.model.Team;
import com.techcup.ccteams.repository.PlayerProfileRepository;
import com.techcup.ccteams.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TeamService — Unit Tests")
public class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private PlayerProfileRepository playerProfileRepository;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(teamRepository, playerProfileRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Team buildTeam(String teamId, String captainId) {
        Team team = new Team();
        team.setId(teamId);
        team.setName("Los Pumas");
        team.setCaptainId(captainId);
        return team;
    }

    private PlayerProfile buildActivePlayer(String userId, String teamId) {
        PlayerProfile p = new PlayerProfile();
        p.setUserId(userId);
        p.setTeamId(teamId);
        p.setShirtNumber(10);
        p.setPosition("Forward");
        p.setIsActive(true);
        p.setIsCaptain(false);
        return p;
    }

    // ── Consultar plantilla ───────────────────────────────────────────────

    @Test
    @DisplayName("Should return team roster when team exists")
    public void testGetTeamRoster_Success() {
        String teamId = "team-123";
        Team team = buildTeam(teamId, "captain-456");
        PlayerProfile player = buildActivePlayer("player-1", teamId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerProfileRepository.findByTeamIdAndIsActiveTrue(teamId))
                .thenReturn(Arrays.asList(player));

        TeamRosterResponse response = teamService.getTeamRoster(teamId);

        assertNotNull(response);
        assertEquals(teamId, response.getTeamId());
        assertEquals(1, response.getTotalPlayers());
        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    @DisplayName("Should throw when team not found on roster query")
    public void testGetTeamRoster_TeamNotFound() {
        when(teamRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> teamService.getTeamRoster("nonexistent"));
    }

    // ── Actualizar equipo ────────────────────────────────────────────────

    @Test
    @DisplayName("Should update team successfully when captain requests")
    public void testUpdateTeam_Success() {
        String teamId    = "team-123";
        String captainId = "captain-456";
        Team team = buildTeam(teamId, captainId);

        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.existsByName("Los Tigres")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamUpdateResponse response = teamService.updateTeam(teamId, captainId, request);

        assertNotNull(response);
        assertEquals(teamId, response.getTeamId());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    @DisplayName("Should throw when non-captain tries to update team")
    public void testUpdateTeam_NotCaptain() {
        String teamId = "team-123";
        Team team = buildTeam(teamId, "real-captain");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");

        assertThrows(BusinessException.class,
                () -> teamService.updateTeam(teamId, "other-user", request));
    }

    @Test
    @DisplayName("Should throw when new team name is already taken")
    public void testUpdateTeam_DuplicateName() {
        String teamId    = "team-123";
        String captainId = "captain-456";
        Team team = buildTeam(teamId, captainId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.existsByName("Los Tigres")).thenReturn(true);

        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("Los Tigres");

        assertThrows(BusinessException.class,
                () -> teamService.updateTeam(teamId, captainId, request));
    }

    // ── TC-26: Retirar jugador ────────────────────────────────────────────

    @Nested
    @DisplayName("TC-26 — Remove player from team")
    class RemovePlayerTests {

        @Test
        @DisplayName("Should remove player successfully when captain requests")
        void shouldRemovePlayerSuccessfully() {
            String teamId    = "team-123";
            String captainId = "captain-456";
            String playerId  = "player-789";

            Team team = buildTeam(teamId, captainId);
            PlayerProfile player = buildActivePlayer(playerId, teamId);

            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
            when(playerProfileRepository.findByUserId(playerId))
                    .thenReturn(Optional.of(player));
            when(playerProfileRepository.save(any(PlayerProfile.class)))
                    .thenReturn(player);

            assertDoesNotThrow(() -> teamService.removePlayer(teamId, playerId, captainId));

            verify(playerProfileRepository).save(argThat(p -> !p.getIsActive()));
        }

        @Test
        @DisplayName("Should throw when team does not exist")
        void shouldThrowWhenTeamNotFound() {
            when(teamRepository.findById("nonexistent")).thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> teamService.removePlayer("nonexistent", "player-1", "captain-1"));
        }

        @Test
        @DisplayName("Should throw when requester is not the captain")
        void shouldThrowWhenNotCaptain() {
            String teamId = "team-123";
            Team team = buildTeam(teamId, "real-captain");

            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

            assertThrows(BusinessException.class,
                    () -> teamService.removePlayer(teamId, "player-1", "other-user"));
        }

        @Test
        @DisplayName("Should throw when captain tries to remove themselves")
        void shouldThrowWhenCaptainRemovesHimself() {
            String teamId    = "team-123";
            String captainId = "captain-456";
            Team team = buildTeam(teamId, captainId);

            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

            assertThrows(BusinessException.class,
                    () -> teamService.removePlayer(teamId, captainId, captainId));
        }

        @Test
        @DisplayName("Should throw when player does not belong to the team")
        void shouldThrowWhenPlayerNotInTeam() {
            String teamId    = "team-123";
            String captainId = "captain-456";
            String playerId  = "player-789";

            Team team = buildTeam(teamId, captainId);
            PlayerProfile player = buildActivePlayer(playerId, "other-team");

            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
            when(playerProfileRepository.findByUserId(playerId))
                    .thenReturn(Optional.of(player));

            assertThrows(BusinessException.class,
                    () -> teamService.removePlayer(teamId, playerId, captainId));
        }

        @Test
        @DisplayName("Should throw when player was already removed")
        void shouldThrowWhenPlayerAlreadyInactive() {
            String teamId    = "team-123";
            String captainId = "captain-456";
            String playerId  = "player-789";

            Team team = buildTeam(teamId, captainId);
            PlayerProfile player = buildActivePlayer(playerId, teamId);
            player.setIsActive(false);

            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
            when(playerProfileRepository.findByUserId(playerId))
                    .thenReturn(Optional.of(player));

            assertThrows(BusinessException.class,
                    () -> teamService.removePlayer(teamId, playerId, captainId));
        }

        @Test
        @DisplayName("Should throw when player profile does not exist")
        void shouldThrowWhenPlayerProfileNotFound() {
            String teamId    = "team-123";
            String captainId = "captain-456";
            Team team = buildTeam(teamId, captainId);

            when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
            when(playerProfileRepository.findByUserId("nonexistent"))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> teamService.removePlayer(teamId, "nonexistent", captainId));
        }
    }
}