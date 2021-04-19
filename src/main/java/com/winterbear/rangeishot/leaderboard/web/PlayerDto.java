package com.winterbear.rangeishot.leaderboard.web;

import lombok.Data;

import java.util.List;

@Data
public class PlayerDto {
    private String id;
    private String steamId;
    private String username;
    List<AccoladeDto> accoladeDtos;
}
