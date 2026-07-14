package com.techcup.ccteams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCardDto {
    private UUID userId;
    private String name;
    private String photoUrl;
    private String position;
    private Integer shirtNumber;
    private Boolean isCaptain;
}