package com.winterbear.rangeishot.leaderboard.repo.entity;

import com.winterbear.rangeishot.leaderboard.repo.entity.ScoreId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.UniqueConstraint;

/**
 * We'll make score a json array of millisecond integers, to keep things simple.   indexed from zero, each round will be corresponding slot
 * the tournament will store the total number of rounds.   when complete we'll score the tournament.
 * Not sure we'll store "position".   That's really easy for a DB to calculate
 *
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tournament tournament;

    private String courseScores;

    private int totalScore;
}
