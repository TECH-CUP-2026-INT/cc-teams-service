package co.edu.escuelaing.techcup.teams.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TeamMemberEntityTest {

    @Test
    void builder_setsFieldsCorrectly() {
        TeamEntity team = TeamEntity.builder()
                .name("Test Team")
                .captainEmail("captain@test.com")
                .build();

        TeamMemberEntity member = TeamMemberEntity.builder()
                .team(team)
                .memberEmail("player@test.com")
                .role(TeamMemberEntity.Role.PLAYER)
                .build();

        assertEquals(team, member.getTeam());
        assertEquals("player@test.com", member.getMemberEmail());
        assertEquals(TeamMemberEntity.Role.PLAYER, member.getRole());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        TeamEntity team = new TeamEntity();
        TeamMemberEntity member = new TeamMemberEntity();

        member.setId(1L);
        member.setTeam(team);
        member.setMemberEmail("user@test.com");
        member.setRole(TeamMemberEntity.Role.CAPTAIN);
        member.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        member.setJoinedAt(now);

        assertEquals(1L, member.getId());
        assertEquals(team, member.getTeam());
        assertEquals("user@test.com", member.getMemberEmail());
        assertEquals(TeamMemberEntity.Role.CAPTAIN, member.getRole());
        assertTrue(member.isActive());
        assertEquals(now, member.getJoinedAt());
    }

    @Test
    void onCreate_setsJoinedAtAndActive() {
        TeamMemberEntity member = new TeamMemberEntity();
        member.setActive(false);

        member.onCreate();

        assertTrue(member.isActive());
        assertNotNull(member.getJoinedAt());
        assertTrue(member.getJoinedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void role_enum_hasTwoValues() {
        TeamMemberEntity.Role[] roles = TeamMemberEntity.Role.values();
        assertEquals(2, roles.length);
        assertEquals(TeamMemberEntity.Role.CAPTAIN, TeamMemberEntity.Role.valueOf("CAPTAIN"));
        assertEquals(TeamMemberEntity.Role.PLAYER, TeamMemberEntity.Role.valueOf("PLAYER"));
    }

    @Test
    void builder_forCaptain_setsRoleCorrectly() {
        TeamMemberEntity captain = TeamMemberEntity.builder()
                .memberEmail("captain@test.com")
                .role(TeamMemberEntity.Role.CAPTAIN)
                .build();

        assertEquals(TeamMemberEntity.Role.CAPTAIN, captain.getRole());
        assertEquals("captain@test.com", captain.getMemberEmail());
    }
}
