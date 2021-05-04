package com.winterbear.rangeishot.leaderboard.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto extends LeaderboardDto {
    private int id;
    private Date start;
    private Date end;
    private String thumbnailPath;
    private String name;
    private String description;
    @Singular
    private List<String> courses;
    @Singular
    private List<TournamentScoreDto> scores;
}
