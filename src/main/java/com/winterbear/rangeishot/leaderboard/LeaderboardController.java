package com.winterbear.rangeishot.leaderboard;

import com.winterbear.rangeishot.leaderboard.web.PlayerDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentScoreDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentSubmitScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
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

    @GetMapping("/scores")
    public List<TournamentScoreDto> getActiveTournaments(@RequestParam("tournament") Integer tournamentId) {
        return leaderboardService.getScoresByTournament(tournamentId);
    }

    @PostMapping("/tournament")
    public TournamentDto createTournament(@RequestBody TournamentDto tournamentDto) throws ParseException {
        return leaderboardService.createTournament(tournamentDto);
    }

    @GetMapping("/tournament/{tournamentId}")
    public TournamentDto getTournament(@PathVariable("tournamentId") Integer tournamentId) {
        return leaderboardService.getTournament(tournamentId);
    }

    @PostMapping("/tournament/{tournamentId}")
    public ResponseEntity<TournamentScoreDto> submitScore(@RequestHeader("ticket") String ticket,
                                                     @PathVariable("tournamentId") int tournamentId,
                                                     @RequestBody TournamentSubmitScoreDto tournamentSubmitScoreDto) {
        TournamentScoreDto tournamentScoreDto = leaderboardService.submitScore(ticket, tournamentId, tournamentSubmitScoreDto);
        return ResponseEntity.ok(tournamentScoreDto);
    }
}
