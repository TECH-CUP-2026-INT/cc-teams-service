package com.techcup.ccteams.dto.request;
import lombok.Data;
import jakarta.validation.constraints.Size;
@Data
public class TeamUpdateRequest {
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String name;
    private String logoUrl;
    private String colors;
}