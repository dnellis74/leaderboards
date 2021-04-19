package com.winterbear.rangeishot.leaderboard.repo;

import com.winterbear.rangeishot.leaderboard.web.TournamentScoreDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private Date open;
    private Date close;
    private String thumbnailPath;
    private String name;
    private String description;
    private String courses;

    @ElementCollection
    private List<TournamentScore> scores;
}
