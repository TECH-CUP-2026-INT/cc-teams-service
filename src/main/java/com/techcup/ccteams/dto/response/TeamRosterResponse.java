package com.techcup.ccteams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRosterResponse {
    private String teamId;
    private String teamName;
    private String logoUrl;
    private String colors;
    private Integer totalPlayers;
    private List<PlayerCardDto> players;
}