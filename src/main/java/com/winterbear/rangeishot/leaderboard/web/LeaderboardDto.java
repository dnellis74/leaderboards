package com.winterbear.rangeishot.leaderboard.web;

import lombok.Data;

@Data
public abstract class LeaderboardDto {
    private boolean success;
    private String message;
}
