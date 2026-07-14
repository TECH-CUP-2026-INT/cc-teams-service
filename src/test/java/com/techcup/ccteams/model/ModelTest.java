package com.techcup.ccteams.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ModelTest {
    @Test
    public void testTeam() {
        Team team = new Team();
        team.setId("team-123");
        team.setName("Los Pumas");
        assertEquals("team-123", team.getId());
        assertEquals("Los Pumas", team.getName());
    }
    @Test
    public void testPlayerProfile() {
        PlayerProfile profile = new PlayerProfile();
        profile.setUserId("player-1");
        profile.setTeamId("team-123");
        assertEquals("player-1", profile.getUserId());
        assertEquals("team-123", profile.getTeamId());
    }
}