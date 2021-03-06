package com.winterbear.rangeishot.leaderboard.repo;

import com.winterbear.rangeishot.leaderboard.repo.entity.Score;
import com.winterbear.rangeishot.leaderboard.repo.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepo extends JpaRepository<Score, Integer> {
    // Spring Data generates this class at runtime

    Optional<Score> findByPlayerId_AndTournament(String s, Tournament tournament);

    List<Score> findByTournamentOrderByTotalScore(Tournament tournamentId);
}
