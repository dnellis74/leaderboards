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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
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

    private final Gson gson = new Gson();

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
        String courseNames = tournamentDto.getCourses().stream()
                .collect(Collectors.joining(","));

        return Tournament.builder()
                .close(endDate)
                .courses(courseNames)
                .description(tournamentDto.getDescription())
                .name(tournamentDto.getName())
                .numCourses(tournamentDto.getCourses().size())
                .open(startDate)
                .thumbnailPath(tournamentDto.getThumbnailPath())
                .build();
    }

    private TournamentDto from(Tournament tournament) {
        List<String> courseNames = Arrays.asList(tournament.getCourses().split(","));
        TournamentDto result = TournamentDto.builder()
                .id(tournament.getId())
                .courses(courseNames)
                .open(tournament.getOpen().toString())
                .close(tournament.getClose().toString())
                .description(tournament.getDescription())
                .name(tournament.getName())
                .thumbnailPath(tournament.getThumbnailPath())
                .build();
        if (tournament.getScores() != null) {
            List<TournamentScoreDto> scoreList = tournament.getScores().stream()
                    .filter(x -> x.getTotalScore() != 0)
                    // Not sure how this works with a steam and hibernate collections
                    .limit(DEFAULT_TOP_SCORES)
                    .map(s -> from(s))
                    .collect(Collectors.toList());
            result.setScores(scoreList);
        }
        return result;
    }

    private TournamentScoreDto from(Score s) {
        List<Integer> scores = getListIntegers(getPArray(s.getCourseScores()));
        return TournamentScoreDto.builder()
                .scores(scores)
                .totalScore(s.getTotalScore())
                .playerId(s.getPlayerId())
                .playerName(s.getPlayerName())
                .build();
    }

    private int[] getPArray(String stringIntArray) {
        return gson.fromJson(stringIntArray, new TypeToken<int[]>() {
        }.getType());
    }

    private List<Integer> getListIntegers(int[] arrayInt) {
        return Arrays.stream(arrayInt).boxed().collect(Collectors.toList());
    }

    public TournamentScoreDto submitScore(String ticket, int tournamentId, TournamentSubmitScoreDto tournamentSubmitScoreDto) {
        // Validate user ticket
        try {
            verifyId(ticket);
        } catch (RuntimeException e) {
            TournamentDto result = new TournamentDto();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            throw new RuntimeException(result.getMessage());
        }

        Tournament tournamentFilter = new Tournament();
        tournamentFilter.setId(Integer.valueOf(tournamentId));
        Optional<Score> score = scoreRepo.findByPlayerId_AndTournament(tournamentSubmitScoreDto.getPlayerId(), tournamentFilter);

        int[] scoreArray;
        if (score.isPresent()) {
            scoreArray = gson.fromJson(score.get().getCourseScores(), new TypeToken<int[]>() {
            }.getType());
        } else {
            Optional<Tournament> tournament = tournamentRepo.findById(tournamentId);
            scoreArray = new int[tournament.get().getNumCourses()];
            score = Optional.of(Score.builder()
                    .playerId(tournamentSubmitScoreDto.getPlayerId())
                    .playerName(tournamentSubmitScoreDto.getPlayerName())
                    .tournament(tournament.get())
                    .build());
        }
        scoreArray[tournamentSubmitScoreDto.getCourse() - 1] = tournamentSubmitScoreDto.getScore();
        int total = 0;
        if (score.get().getTotalScore() != 0 || !Arrays.stream(scoreArray).anyMatch(x -> x == 0)) {
            total = Arrays.stream(scoreArray).sum();
        }

        String scoresString = gson.toJson(scoreArray);
        score.get().setCourseScores(scoresString);
        score.get().setTotalScore(total);
        scoreRepo.saveAndFlush(score.get());

        List<Integer> scoreList = Arrays.stream(scoreArray).boxed().collect(Collectors.toList());
        TournamentScoreDto tournamentScoreDto = TournamentScoreDto.builder()
                .playerId(tournamentSubmitScoreDto.getPlayerId())
                .playerName(tournamentSubmitScoreDto.getPlayerName())
                .scores(scoreList)
                .totalScore(total)
                .build();
        return tournamentScoreDto;
    }

    private boolean verifyId(String ticket) {
        String urlString = String.format(AUTHENTICATE_USER_TICKET, publisherKey, appId, ticket);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ApiResponse> responseEntity = restTemplate.getForEntity(urlString, ApiResponse.class);
        ApiResponse apiResponse = responseEntity.getBody();
        if (responseEntity.getStatusCode() != HttpStatus.OK
                || apiResponse.getResponse().getError() != null) {
            if (ticketValidation) {
                throw new RuntimeException(apiResponse.getResponse().getError().getErrordesc());
            } else {
                log.warn("Ticket header validation failed for {}\n{}", ticket, apiResponse.getResponse().getError().getErrordesc());
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
