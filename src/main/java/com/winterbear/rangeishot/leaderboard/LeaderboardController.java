package com.winterbear.rangeishot.leaderboard;

import com.winterbear.rangeishot.leaderboard.web.PlayerDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;


@RestController
public class LeaderboardController {

    private LeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/login")
    public PlayerDto authenticate(@RequestBody String id) {
        return new PlayerDto();
    }

    @GetMapping("/tournament")
    public List<TournamentDto> getActiveTournaments() {
        return leaderboardService.getTournaments();
    }

    @PostMapping("/tournament")
    public TournamentDto createTournament(@RequestBody TournamentDto tournamentDto) {
        return leaderboardService.createTournament(tournamentDto);
    }

    @GetMapping("/tournament/{tournamentId}")
    public TournamentDto getTournament(@PathParam("tournamentId") String tournamentId) {
        return null;
    }

    @PostMapping("/tournament/{tournamentId}")
    public TournamentDto submitScore(@RequestBody int score, @PathParam("tournamentId") String tournamentId) {
        return null;
    }
}
