package com.winterbear.rangeishot.leaderboard.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TournamentDto extends LeaderboardDto {
    private int id;
    private String open;
    private String close;
    private String thumbnailPath;
    private String name;
    private String description;
    private List<String> courses;
    @Singular
    private List<TournamentScoreDto> scores;
}
