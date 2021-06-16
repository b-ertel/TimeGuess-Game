package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;

/**
 * Service for logic actions during a running game.
 */
@Component
@Scope("application")
public class GameLogicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameLogicService.class);

    @Autowired
    TermService termService;

    @Autowired
    RoundService roundService;

    /**
     * method to check whether all terms of a topic have been used or not
     * @param  game game
     * @return boolean whether there a still terms available or not
     */
    public boolean stillTermsAvailable(Game game) {
        List<Term> availableTerms = termService.getAllEnabledTermsOfTopic(game.getTopic());
        Set<Term> usedTerms = usedTerms(game);
        return !(availableTerms.size() == usedTerms.size());
    }

    /**
     * method to check wheter a team have reached the max points of a game
     * @param  game with a maximum of points
     * @param  team to check
     * @return true if it did, false if not
     */
    public boolean teamReachedMaxPoints(Game game, Team team) {
        return game.getMaxPoints() <= roundService.getPointsOfTeamInGame(game, team);
    }

    /**
     * method to evaluate the team with the most points in a game
     * @param  game game to evaluate
     * @return team with the most points
     */
    public Team getTeamWithMostPoints(Game game) {
        Integer points = 0;
        Team team = null;
        boolean draw = false;
        for (Team t : game.getTeams()) {
            Integer pointsOfTeamInGame = roundService.getPointsOfTeamInGame(game, t);
            if (pointsOfTeamInGame == points) {
                draw = true;
            }
            else if (pointsOfTeamInGame > points) {
                team = t;
                points = pointsOfTeamInGame;
                draw = false;
            }
        }
        return draw ? null : team;
    }

    /**
     * method to estimate which terms have been used
     * @param  game current game
     * @return set with all terms, that have been used in the current game
     */
    public Set<Term> usedTerms(Game game) {
        Set<Term> usedTerms = new HashSet<>();
        game.getRounds().stream().forEach(round -> usedTerms.add(round.getTermToGuess()));
        return usedTerms;
    }

    /**
     * method to choose randomly a term from the one, that where not already picked in previous rounds
     * @param  game game
     * @return term to guess
     */
    public Term nextTerm(Game game) {
        List<Term> terms = termService.getAllTermsOfTopic(game.getTopic());
        Set<Term> usedTerms = usedTerms(game);
        usedTerms.stream().forEach(term -> terms.remove(term));
        Random rand = new Random();
        Term next = terms.get(Math.abs(rand.nextInt(terms.size())));
        while (!next.isEnabled()) {
            Random newRand = new Random();
            next = terms.get(Math.abs(newRand.nextInt(terms.size())));
        }
        return next;
    }

    /**
     * Method to estimate which team comes next. If no rounds played in game, a random team
     * is chosen. If a round is played, the next team in the list is returned
     * @param  game game to evaluate next team
     * @return team that plays next
     */
    public Team getNextTeam(Game game) {
        List<Team> teams = new ArrayList<>(game.getTeams());
        if (roundService.roundsPlayedInGame(game)) {
            Team teamOfLastRound = roundService.getLastRound(game).getGuessingTeam();
            int index = 0;
            for (Team t : teams) {
                if (t.getName() == teamOfLastRound.getName()) {
                    index = teams.indexOf(t);
                }
            }
            if (index == (teams.size() - 1)) {
                return teams.get(0);
            }
            else {
                return teams.get(index + 1);
            }
        }
        else {
            Random rand = new Random();
            return teams.get(Math.abs(rand.nextInt(teams.size())));
        }
    }

    /**
     * Method to estimate which user comes next. If team of user has not played any rounds in game, a random user
     * is chosen. If a round is played by the team , the next user in the list is returned
     * @param  game game to evaluate next team
     * @param  team team of users
     * @return user that plays next
     */
    public User nextUser(Game game, Team team) {
        List<User> users = new ArrayList<>();
        Iterator<User> ite = team.getTeamMembers().iterator();
        while (ite.hasNext()) {
            users.add(ite.next());
        }
        if (roundService.teamPlayedRoundsInGame(game, team)) {
            User lastUser = roundService.getLastRoundOfTeam(game, team).getGuessingUser();
            int index = 0;
            for (User u : users) {
                if (u.getUsername() == lastUser.getUsername()) {
                    index = users.indexOf(u);
                }
            }
            if (index == (users.size() - 1)) {
                return users.get(0);
            }
            else {
                return users.get(index + 1);
            }
        }
        else {
            Random rand = new Random();
            return users.get(rand.nextInt(users.size()));
        }
    }

    /**
     * Method to create next round of a game.
     * Sets all values of {@link Round} except for points, time and activity
     * @param  game game to evaluate
     * @return next round
     */
    public Round getNextRound(Game game) {
        Round nextRound = new Round();
        nextRound.setNr(game.getRounds().size() + 1);
        Team nextTeam = getNextTeam(game);
        nextRound.setGuessingUser(nextUser(game, nextTeam));
        nextRound.setGuessingTeam(nextTeam);
        Set<Team> verifiyingTeams = game.getTeams();
        verifiyingTeams.remove(nextTeam);
        nextRound.setVerifyingTeams(verifiyingTeams);
        nextRound.setTermToGuess(nextTerm(game));
        LOGGER.info("New Round nr '{}', with team '{}' and user '{}' was created", nextRound.getNr(),
            nextRound.getGuessingTeam().getName(), nextRound.getGuessingUser().getUsername());
        return nextRound;
    }

    /**
     * Method that sets informations of cube-face into round.
     * @param  round    round that gets informations
     * @param  cubeFace cube face that delivers informations
     * @return updated round
     */
    public Round getCubeInfosIntoRound(Round round, CubeFace cubeFace) {
        round.setActivity(cubeFace.getActivity());
        round.setPoints(cubeFace.getPoints());
        round.setTime(cubeFace.getTime());
        return round;
    }

    /**
     * Method to repeat the given round of a game.
     * Sets a new term but leaves all other values intact.
     * @param  game game to evaluate
     * @param  round round to update
     * @return given round with new term
     */
    public Round repeatRound(Game game, Round round) {
        round.setTermToGuess(nextTerm(game));

        LOGGER.info("Round nr '{}', with team '{}' and user '{}' will be repeated", round.getNr(),
            round.getGuessingTeam().getName(), round.getGuessingUser().getUsername());
        return round;
    }

    /**
     * Method that sets validated information into round and adds it to given games round set.
     * @param game  game
     * @param round round
     * @param v     validation
     */
    public void validateRound(Game game, Round round, Validation v) {
        if (round != null) {
            if (v == Validation.CORRECT) {
                round.setCorrectAnswer(true);
            }
            else {
                round.setPoints(v == Validation.CHEATED ? -1 : 0);
            }
            round.setGame(game);
            game.getRounds().add(round);
            game.setRoundNr(game.getRoundNr() + 1);

            LOGGER.info("Round nr '{}', with team '{}' is ready to be saved, gamerounds '{}', points '{}'",
                round.getNr(), round.getGuessingTeam().getName(), game.getRounds().size(), round.getPoints());
        }
    }
}
