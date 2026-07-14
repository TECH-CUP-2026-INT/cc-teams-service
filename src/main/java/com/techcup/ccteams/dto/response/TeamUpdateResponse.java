package com.techcup.ccteams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateResponse {
    private UUID teamId;
    private String name;
    private String logoUrl;
    private String colors;
    private String message;
}