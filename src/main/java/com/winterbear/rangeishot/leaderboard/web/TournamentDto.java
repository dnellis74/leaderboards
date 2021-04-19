package com.winterbear.rangeishot.leaderboard.web;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class TournamentDto {
    private int id;
    private Date start;
    private Date end;
    private String thumbnailPath;
    private String name;
    private String description;
    private List<String> courses;
    private List<TournamentScoreDto> scores;
}
