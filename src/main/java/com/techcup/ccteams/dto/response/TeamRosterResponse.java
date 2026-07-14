package com.techcup.ccteams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRosterResponse {
    private UUID teamId;
    private String teamName;
    private String logoUrl;
    private String colors;
    private Integer totalPlayers;
    private List<PlayerCardDto> players;
}