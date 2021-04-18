package com.winterbear.rangeishot.leaderboard.web;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Tournament {
    String id;
    Date start;
    Date end;
    String thumbnailPath;
    String name;
    String description;
    List<String> courses;
    List<TournamentScore> scores;


}
