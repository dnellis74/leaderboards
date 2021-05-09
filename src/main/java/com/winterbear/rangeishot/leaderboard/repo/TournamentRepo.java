package com.winterbear.rangeishot.leaderboard.repo;

import com.winterbear.rangeishot.leaderboard.repo.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TournamentRepo extends JpaRepository<Tournament, Integer> {
    // Spring Data generates this class at runtime


}
