package com.winterbear.rangeishot.leaderboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winterbear.rangeishot.leaderboard.repo.ScoreRepo;
import com.winterbear.rangeishot.leaderboard.repo.TournamentRepo;
import com.winterbear.rangeishot.leaderboard.repo.entity.Score;
import com.winterbear.rangeishot.leaderboard.repo.entity.Tournament;
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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    public static final String AUTHENTICATE_USER_TICKET = "https://partner.steam-api.com/ISteamUserAuth/AuthenticateUserTicket/v1/?key=%s&appid=%s&ticket=%s";
    public static final int DEFAULT_TOP_SCORES = 3;

    private TournamentRepo tournamentRepo;

    private ScoreRepo scoreRepo;

    @Value("${com.winterbear.publisherkey}")
    String publisherKey;

    @Value("${com.winterbear.appid:1503590}")
    String appId;

    @Value("${com.winterbear.validation.ticket:true}")
    Boolean ticketValidation;

    @Autowired
    public LeaderboardService(TournamentRepo tournamentRepo, ScoreRepo scoreRepo) {
        this.tournamentRepo = tournamentRepo;
        this.scoreRepo = scoreRepo;
    }

    public TournamentDto createTournament(TournamentDto tournamentDto) throws ParseException {
        Tournament tournament = from(tournamentDto);
        Tournament resultEntity = tournamentRepo.save(tournament);
        TournamentDto resultDto = from(resultEntity);
        resultDto.setSuccess(true);
        return resultDto;
    }

    public List<TournamentDto> getTournaments() {
        List<Tournament> tournaments = tournamentRepo.findAll();
        return tournaments.stream()
                .map(p -> from(p))
                .collect(Collectors.toList());
    }

    public TournamentDto getTournament(Integer tournamentId) {
        Optional<Tournament> tournament = tournamentRepo.findById(tournamentId);
        return from(tournament.get());
    }

    private Tournament from(TournamentDto tournamentDto) throws ParseException {
        LocalDate endDate = LocalDate.parse(tournamentDto.getClose());
        LocalDate startDate = LocalDate.parse(tournamentDto.getOpen());

        return Tournament.builder()
                .close(endDate)
                .courses(tournamentDto.getCourses())
                .description(tournamentDto.getDescription())
                .name(tournamentDto.getName())
                .numCourses(tournamentDto.getNumCourses())
                .open(startDate)
                .thumbnailPath(tournamentDto.getThumbnailPath())
                .build();
    }

    private TournamentDto from(Tournament tournament) {
        TournamentDto result = TournamentDto.builder()
                .id(tournament.getId())
                .courses(tournament.getCourses())
                .open(tournament.getOpen().toString())
                .close(tournament.getClose().toString())
                .numCourses(tournament.getNumCourses())
                .description(tournament.getDescription())
                .name(tournament.getName())
                .thumbnailPath(tournament.getThumbnailPath())
                .build();
        if (tournament.getScores() != null) {
            List<TournamentScoreDto> scoreList = tournament.getScores().stream()
                    // Not sure how this works with a steam and hibernate collections
                    .limit(DEFAULT_TOP_SCORES)
                    .map(s -> from(s))
                    .collect(Collectors.toList());
            result.setScores(scoreList);
        }
        return result;
    }

    private TournamentScoreDto from(Score s) {
        return TournamentScoreDto.builder()
                .scores(s.getCourseScores())
                .totalScore(s.getTotalScore())
                .playerId(s.getPlayerId())
                .build();
    }

    public TournamentScoreDto submitScore(String ticket, int tournamentId, TournamentSubmitScoreDto tournamentSubmitScoreDto) {
        Gson gson = new Gson();

        // Validate user ticket
        try {
            verifyId(ticket);
        } catch (RuntimeException e) {
            TournamentDto result = new TournamentDto();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            throw new RuntimeException(result.getMessage());
        }

        Tournament tournament1 = new Tournament();
        tournament1.setId(Integer.valueOf(tournamentId));

        Optional<Score> score = scoreRepo.findByPlayerId_AndTournament(tournamentSubmitScoreDto.getPlayerId(), tournament1);

        int[] scoreArray;
        if (score.isPresent()) {
            scoreArray = gson.fromJson(score.get().getCourseScores(), new TypeToken<int[]>() {
            }.getType());
        } else {
            //TODO How to properly initialize tournament numCourses
            Optional<Tournament> tournament = tournamentRepo.findById(tournamentId);
            scoreArray = new int[tournament.get().getNumCourses()];
            score = Optional.of(Score.builder()
                    .playerId(tournamentSubmitScoreDto.getPlayerId())
                    .tournament(tournament.get())
                    .build());
        }
        scoreArray[tournamentSubmitScoreDto.getCourse() - 1] = tournamentSubmitScoreDto.getScore();
        int total = Arrays.stream(scoreArray)
                .sum();
        String scoresString = gson.toJson(scoreArray);
        score.get().setCourseScores(scoresString);
        score.get().setTotalScore(total);
        scoreRepo.saveAndFlush(score.get());

        TournamentScoreDto tournamentScoreDto = TournamentScoreDto.builder()
                .playerId(tournamentSubmitScoreDto.getPlayerId())
                .scores(scoresString)
                .totalScore(total)
                .build();
        return tournamentScoreDto;
    }

    private boolean verifyId(String ticket) {
        if (ticketValidation) {
            String urlString = String.format(AUTHENTICATE_USER_TICKET, publisherKey, appId, ticket);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ApiResponse> responseEntity = restTemplate.getForEntity(urlString, ApiResponse.class);
            ApiResponse apiResponse = responseEntity.getBody();
            if (responseEntity.getStatusCode() != HttpStatus.OK
                    || apiResponse.getResponse().getError() != null) {
                throw new RuntimeException(apiResponse.getResponse().getError().getErrordesc());
            }
        }
        return true;
    }

    public List<TournamentScoreDto> getScoresByTournament(Integer tournamentId) {
        Tournament tournament = new Tournament();
        tournament.setId(Integer.valueOf(tournamentId));
        List<Score> byTournament = scoreRepo.findByTournamentOrderByTotalScore(tournament);
        return byTournament.stream()
                .map(p -> from(p))
                .collect(Collectors.toList());
    }
}
