package com.winterbear.rangeishot.leaderboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winterbear.rangeishot.leaderboard.repo.entity.ScoreId;
import com.winterbear.rangeishot.leaderboard.repo.ScoreRepo;
import com.winterbear.rangeishot.leaderboard.repo.entity.Tournament;
import com.winterbear.rangeishot.leaderboard.repo.TournamentRepo;
import com.winterbear.rangeishot.leaderboard.repo.entity.Score;
import com.winterbear.rangeishot.leaderboard.steam.ApiResponse;
import com.winterbear.rangeishot.leaderboard.web.TournamentDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentScoreDto;
import com.winterbear.rangeishot.leaderboard.web.TournamentSubmitScoreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    public static final String AUTHENTICATE_USER_TICKET = "https://partner.steam-api.com/ISteamUserAuth/AuthenticateUserTicket/v1/?key=%s&appid=%s&ticket=%s";

    private TournamentRepo tournamentRepo;

    private ScoreRepo scoreRepo;

    @Value("${com.winterbear.publisherkey}")
    String publisherKey;

    @Value("${com.winterbear.appid}")
    String appId;

    @Autowired
    public LeaderboardService(TournamentRepo tournamentRepo, ScoreRepo scoreRepo) {
        this.tournamentRepo = tournamentRepo;
        this.scoreRepo = scoreRepo;
    }

    public TournamentDto createTournament(TournamentDto tournamentDto) {
        Tournament tournament = from(tournamentDto);
        Tournament resultEntity= tournamentRepo.save(tournament);
        TournamentDto resultDto = from(resultEntity);
        resultDto.setSuccess(true);
        return resultDto;
    }

    public List<TournamentDto> getTournaments() {
        ;
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

    public TournamentDto submitScore(String ticket, String tournamentId, TournamentSubmitScoreDto tournamentSubmitScoreDto) {
        Gson gson = new Gson();
        try {
            verifyId(ticket);
        } catch (RuntimeException e) {
            TournamentDto result = new TournamentDto();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }

        Tournament tournament = new Tournament();
        tournament.setId(Integer.valueOf(tournamentId));
        Optional<Score> score = scoreRepo.findByPlayerId_AndTournament(tournamentSubmitScoreDto.getPlayerId(), tournament);
        Integer[] scoreArray;
        if (score.isPresent()) {
            scoreArray = gson.fromJson(score.get().getCourseScores(), new TypeToken<Integer[]>() {
            }.getType());
        } else {
            scoreArray = new Integer[3];
            score = Optional.of(Score.builder()
                    .playerId(tournamentSubmitScoreDto.getPlayerId())
                    .tournament(tournament)
                    .build());
        }
        scoreArray[tournamentSubmitScoreDto.getCourse()-1] = tournamentSubmitScoreDto.getScore();
        int total = Arrays.stream(scoreArray)
                .filter(p -> Objects.nonNull(p))
                .mapToInt(Integer::intValue)
                .sum();
        String scoresString = gson.toJson(scoreArray);
        score.get().setCourseScores(scoresString);
        score.get().setTotalScore(total);
        scoreRepo.saveAndFlush(score.get());

        TournamentScoreDto tournamentScoreDto = TournamentScoreDto.builder()
                .scores(scoresString)
                .totalScore(total)
                .build();
        TournamentDto result = TournamentDto.builder()
                .score(tournamentScoreDto)
                .build();
        result.setSuccess(true);
        return result;
    }

    private boolean verifyId(String ticket) {
        String urlString = String.format(AUTHENTICATE_USER_TICKET, publisherKey, appId, ticket);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ApiResponse> responseEntity = restTemplate.getForEntity(urlString, ApiResponse.class);
        ApiResponse apiResponse = responseEntity.getBody();
        if (responseEntity.getStatusCode() != HttpStatus.OK
                || apiResponse.getResponse().getError() != null) {
            //throw new RuntimeException(apiResponse.getResponse().getError().getErrordesc());
        }
        return true;
    }
}
