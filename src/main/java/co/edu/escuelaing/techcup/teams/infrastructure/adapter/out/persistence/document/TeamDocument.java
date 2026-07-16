package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "teams")
public class TeamDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String name;

    private Binary logo;
    private String logoContentType;
    private String colors;

    @Indexed
    private UUID captainId;

    private List<TeamMemberDocument> members;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
