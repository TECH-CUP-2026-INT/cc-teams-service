package com.techcup.ccteams.service;
import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.exception.BusinessException;
import com.techcup.ccteams.model.PlayerProfile;
import com.techcup.ccteams.model.Team;
import com.techcup.ccteams.repository.PlayerProfileRepository;
import com.techcup.ccteams.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PlayerProfileRepository playerProfileRepository;
    @InjectMocks
    private TeamService teamService;
    @Test
    public void testGetTeamRoster_Success() {
        String teamId = "team-123";
        Team team = new Team();
        team.setId(teamId);
        team.setName("Los Pumas");
        PlayerProfile player = new PlayerProfile();
        player.setUserId("player-1");
        player.setTeamId(teamId);
        player.setShirtNumber(10);
        player.setPosition("Forward");
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerProfileRepository.findByTeamIdAndIsActiveTrue(teamId)).thenReturn(Arrays.asList(player));
        TeamRosterResponse response = teamService.getTeamRoster(teamId);
        assertNotNull(response);
        assertEquals(teamId, response.getTeamId());
        assertEquals(1, response.getTotalPlayers());
        verify(teamRepository, times(1)).findById(teamId);
    }
    @Test
    public void testUpdateTeam_Success() {
        String teamId = "team-123";
        String captainId = "captain-456";
        Team team = new Team();
        team.setId(teamId);
        team.setName("Los Pumas");
        team.setCaptainId(captainId);
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
}