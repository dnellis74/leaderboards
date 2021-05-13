package com.winterbear.rangeishot.leaderboard.web;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TournamentSubmitScoreDto {
    private String playerId;
    private String playerName;
    private int course;
    private int score;
}
