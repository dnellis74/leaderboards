package com.winterbear.rangeishot.leaderboard.repo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ScoreId implements Serializable {
    private String tournamentId;

    private String playerId;
}
