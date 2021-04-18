package com.winterbear.rangeishot.leaderboard.web;

import lombok.Data;

import java.util.List;

@Data
public class TournamentScore {
    private String playerId;
    private String tournamentId;
    private int position;
    private List<Integer> scores;
    private int totalScore;
}
