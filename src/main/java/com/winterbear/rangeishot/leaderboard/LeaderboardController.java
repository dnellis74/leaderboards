package com.winterbear.rangeishot.leaderboard;

import com.winterbear.rangeishot.leaderboard.web.Player;
import com.winterbear.rangeishot.leaderboard.web.Tournament;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;


@RestController
public class LeaderboardController {

    @PostMapping("/login")
    public Player authenticate(@RequestBody String id) {
        return new Player();
    }

    @GetMapping
    public List<Tournament> getActiveTournaments() {
        return Collections.emptyList();
    }

    @GetMapping("/tournament/{tournamentId}")
    public Tournament getTournament(@PathParam("tournamentId") String tournamentId) {
        return new Tournament();
    }

    @PostMapping("/tournament/{tournamentId}")
    public Tournament submitScore(@RequestBody int score, @PathParam("tournamentId") String tournamentId) {
        return new Tournament();
    }
}
