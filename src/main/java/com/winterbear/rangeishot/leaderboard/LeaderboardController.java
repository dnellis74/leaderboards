package com.winterbear.rangeishot.leaderboard;

import com.winterbear.rangeishot.leaderboard.web.PlayerDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentSubmitScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
    public TournamentDto getTournament(@PathVariable("tournamentId") String tournamentId) {
        return leaderboardService.getTournament(tournamentId);
    }

    @PostMapping("/tournament/{tournamentId}")
    public ResponseEntity<TournamentDto> submitScore(@RequestHeader("ticket") String ticket,
                                                     @PathVariable("tournamentId") String tournamentId,
                                                     @RequestBody TournamentSubmitScoreDto tournamentSubmitScoreDto) {
        TournamentDto tournamentDto = leaderboardService.submitScore(ticket, tournamentId, tournamentSubmitScoreDto);
        return ResponseEntity.ok(tournamentDto);
    }
}
