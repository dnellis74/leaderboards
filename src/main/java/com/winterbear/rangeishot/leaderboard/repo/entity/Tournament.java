package com.winterbear.rangeishot.leaderboard.repo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Date open;
    private Date close;
    private String thumbnailPath;
    private String name;
    private String description;
    private String numCourses;
    private String courses;

    @OneToMany(mappedBy="tournament", fetch = FetchType.LAZY)
    @OrderBy("totalScore")
    private List<Score> scores;
}
