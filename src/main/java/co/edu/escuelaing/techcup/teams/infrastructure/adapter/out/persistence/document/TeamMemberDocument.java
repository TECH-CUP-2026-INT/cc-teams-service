package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document;

import co.edu.escuelaing.techcup.teams.domain.enums.TeamMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDocument {

    private String userId;
    private String fullName;
    private TeamMemberRole role;
    private LocalDateTime joinedAt;
}
