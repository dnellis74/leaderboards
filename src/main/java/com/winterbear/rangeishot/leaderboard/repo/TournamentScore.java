package com.winterbear.rangeishot.leaderboard.repo;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class TournamentScore {
    private String playerId;
    private int position;
    private String scores;
    private int totalScore;
}
