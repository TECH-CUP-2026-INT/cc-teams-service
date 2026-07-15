package co.edu.escuelaing.techcup.teams.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    private String id;
    private String name;
    private byte[] logo;
    private String logoContentType;
    private String colors;
    private String captainId;

    @Builder.Default
    private List<TeamMember> members = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static final int MAX_MEMBERS = 12;
    public static final int MIN_MEMBERS_FOR_TOURNAMENT = 7;

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public boolean hasMember(String userId) {
        return members.stream().anyMatch(m -> m.getUserId().equals(userId));
    }
}
