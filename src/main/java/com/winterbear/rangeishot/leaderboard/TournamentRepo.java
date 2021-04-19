package com.winterbear.rangeishot.leaderboard;

import com.winterbear.rangeishot.leaderboard.repo.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepo extends JpaRepository<Tournament, String> {

}
