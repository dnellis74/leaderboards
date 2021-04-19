package com.winterbear.rangeishot.leaderboard;

import com.winterbear.rangeishot.leaderboard.repo.Tournament;
import com.winterbear.rangeishot.leaderboard.web.TournamentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private TournamentRepo tournamentRepo;

    @Autowired
    public LeaderboardService(TournamentRepo tournamentRepo) {
        this.tournamentRepo = tournamentRepo;
    }

    public TournamentDto createTournament(TournamentDto tournamentDto) {
        Tournament tournament = from(tournamentDto);
        Tournament result = tournamentRepo.save(tournament);
        return from(result);
    }

    public List<TournamentDto> getTournaments() {;
        List<Tournament> tournaments = tournamentRepo.findAll();
        return tournaments.stream()
                .map(p -> from(p))
                .collect(Collectors.toList());
    }

    private Tournament from(TournamentDto tournamentDto) {
        return Tournament.builder()
                .name(tournamentDto.getName())
                .build();
    }

    private TournamentDto from(Tournament tournament) {
        return TournamentDto.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .build();
    }
}
