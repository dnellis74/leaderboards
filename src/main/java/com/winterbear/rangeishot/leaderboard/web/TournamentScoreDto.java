package com.winterbear.rangeishot.leaderboard.web;

import com.winterbear.rangeishot.leaderboard.repo.entity.ScoreId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentScoreDto {
    private String playerId;
    private String scores;
    private int totalScore;
}
