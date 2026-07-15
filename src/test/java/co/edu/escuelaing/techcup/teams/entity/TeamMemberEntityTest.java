package co.edu.escuelaing.techcup.teams.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TeamMemberEntityTest {

    @Test
    void builder_setsFieldsCorrectly() {
        TeamMemberEntity member = TeamMemberEntity.builder()
                .teamId("team-1")
                .memberEmail("player@test.com")
                .role(TeamMemberEntity.Role.PLAYER)
                .build();

        assertEquals("team-1", member.getTeamId());
        assertEquals("player@test.com", member.getMemberEmail());
        assertEquals(TeamMemberEntity.Role.PLAYER, member.getRole());
        assertTrue(member.isActive());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        TeamMemberEntity member = new TeamMemberEntity();

        member.setId("member-1");
        member.setTeamId("team-1");
        member.setMemberEmail("user@test.com");
        member.setRole(TeamMemberEntity.Role.CAPTAIN);
        member.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        member.setJoinedAt(now);

        assertEquals("member-1", member.getId());
        assertEquals("team-1", member.getTeamId());
        assertEquals("user@test.com", member.getMemberEmail());
        assertEquals(TeamMemberEntity.Role.CAPTAIN, member.getRole());
        assertTrue(member.isActive());
        assertEquals(now, member.getJoinedAt());
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
                .teamId("team-1")
                .memberEmail("captain@test.com")
                .role(TeamMemberEntity.Role.CAPTAIN)
                .build();

        assertEquals(TeamMemberEntity.Role.CAPTAIN, captain.getRole());
        assertEquals("captain@test.com", captain.getMemberEmail());
    }

    @Test
    void builder_setsActiveTrue() {
        TeamMemberEntity member = TeamMemberEntity.builder()
                .teamId("team-1")
                .memberEmail("player@test.com")
                .role(TeamMemberEntity.Role.PLAYER)
                .build();

        assertTrue(member.isActive());
    }
}
