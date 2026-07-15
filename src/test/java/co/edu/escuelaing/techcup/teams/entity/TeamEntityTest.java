package co.edu.escuelaing.techcup.teams.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TeamEntityTest {

    @Test
    void builder_setsFieldsCorrectly() {
        TeamEntity team = TeamEntity.builder()
                .name("TechCup Team")
                .logoUrl("https://example.com/logo.png")
                .captainEmail("captain@test.com")
                .build();

        assertEquals("TechCup Team", team.getName());
        assertEquals("https://example.com/logo.png", team.getLogoUrl());
        assertEquals("captain@test.com", team.getCaptainEmail());
        assertTrue(team.isActive());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        TeamEntity team = new TeamEntity();

        team.setId("abc-123");
        team.setName("Updated Team");
        team.setLogoUrl("https://example.com/new-logo.png");
        team.setCaptainEmail("new@test.com");
        team.setActive(true);

        LocalDateTime now = LocalDateTime.now();
        team.setCreatedAt(now);
        team.setUpdatedAt(now);

        assertEquals("abc-123", team.getId());
        assertEquals("Updated Team", team.getName());
        assertEquals("https://example.com/new-logo.png", team.getLogoUrl());
        assertEquals("new@test.com", team.getCaptainEmail());
        assertTrue(team.isActive());
        assertEquals(now, team.getCreatedAt());
        assertEquals(now, team.getUpdatedAt());
    }

    @Test
    void builder_withNullLogoUrl_works() {
        TeamEntity team = TeamEntity.builder()
                .name("No Logo Team")
                .captainEmail("captain@test.com")
                .build();

        assertEquals("No Logo Team", team.getName());
        assertNull(team.getLogoUrl());
    }

    @Test
    void builder_setsActiveTrue() {
        TeamEntity team = TeamEntity.builder()
                .name("Active Team")
                .captainEmail("captain@test.com")
                .build();

        assertTrue(team.isActive());
    }
}
