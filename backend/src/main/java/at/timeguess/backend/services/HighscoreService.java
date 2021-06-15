package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.model.utils.PlayerGamesComparator;
import at.timeguess.backend.model.utils.TeamScore;
import at.timeguess.backend.model.utils.TeamScoreComparator;
import at.timeguess.backend.model.utils.UserScores;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.UserRepository;

/**
 * Service for getting game results.
 */
@Component
@Scope("application")
public class HighscoreService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoundRepository roundRepo;

    @Autowired
    RoundService roundService;

    /**
     * Method, that puts informations about number of games won, correct/incorrect answers of a user into the UserScore-class and
     * returns a list of the scores of all users
     * @return list with scores of all users
     */
    public List<UserScores> getUserScores() {
        List<User> users = userRepo.findAll();
        List<UserScores> scores = new ArrayList<>();
        users.stream().filter(user -> user.getRoles().contains(UserRole.PLAYER)).forEach(user -> 
            scores.add(new UserScores(user, userService.getTotalGamesWon(user),
                roundRepo.getNrOfCorrectAnswerByUser(user), roundRepo.getNrOfIncorrectAnswerByUser(user))));
        return scores;
    }

    /**
     * sorts the list of all userscores by number of total games won and correct terms and sorts it to get a highscore-list
     * @return sorted list of userscores
     */
    public List<UserScores> getHighscoresByGamesWon() {
        List<UserScores> ls = getUserScores();
        PlayerGamesComparator comp = new PlayerGamesComparator();
        ls.sort(comp);
        return ls;
    }

    public List<TeamScore> getTeamRanking(Game game) {
        List<TeamScore> ls = new ArrayList<>();
        for (Team team : game.getTeams()) {
            TeamScore score = new TeamScore(team, roundService.getPointsOfTeamInGame(game, team));
            ls.add(score);
        }
        TeamScoreComparator comp = new TeamScoreComparator();
        ls.sort(comp);
        return ls;
    }

}
