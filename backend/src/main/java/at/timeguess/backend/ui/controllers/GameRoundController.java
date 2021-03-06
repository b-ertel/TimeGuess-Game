package at.timeguess.backend.ui.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.model.utils.TeamScore;
import at.timeguess.backend.services.HighscoreService;
import at.timeguess.backend.ui.controllers.GameManagerController.NoGameReason;
import at.timeguess.backend.ui.controllers.GameManagerController.RoundState;
import at.timeguess.backend.ui.controllers.GameManagerController.WaitReason;

/**
 * This controller holds the current game content for a given user.
 */
@Controller
@Scope("session")
public class GameRoundController {

    /**
     * Possible states for a game displayed.
     */
    public enum RunState {
        /**
         * Game cannot be run, check {@link NoGameReason}.
         */
        NONE,
        /**
         * Game has to wait for something to happen, check {@link WaitReason}.
         */
        WAITING,
        /**
         * Game is running, check {@link RoundState}.
         */
        RUNNING
    }

    @Autowired
    private GameManagerController gameManagerController;
    @Autowired
    private HighscoreService highscoreService;

    private User user = null;
    private Game currentGame = null;
    private Team currentTeam = null;
    private Round currentRound;
    private Round lastRound;

    public User getUser() {
        return user;
    }

    /**
     * Sets the user for which a game is displayed.
     * Needs to be set on entering the game page to update values correctly.
     * @param user user
     */
    public void setUser(User user) {
        this.user = user;

        destroy();
        updateRound();
    }

    public Game getCurrentGame() {
        if (currentGame == null) {
            currentGame = gameManagerController.getCurrentGameForUser(user);
        }
        return currentGame;
    }

    public Team getCurrentTeam() {
        if (currentTeam == null && getCurrentGame() != null) {
            currentTeam = currentGame.getTeams().stream()
                .filter(t -> t.getTeamMembers().contains(user)).findFirst().orElse(null);
        }
        return currentTeam;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public Round getLastRound() {
        return lastRound;
    }

    /**
     * Method to get a set of all teams in the current game
     * @return set of teams
     */
    public Set<Team> getTeamsInGame() {
        return currentGame.getTeams();
    }

    /**
     * Method to check if given team is current round's guessing team.
     * @param  team team
     * @return true if it is, false if not
     */
    public boolean isGuessingTeam(Team team) {
        Team guessteam = currentRound == null ? null : currentRound.getGuessingTeam();
        return guessteam == null ? false : guessteam.equals(team);
    }

    /**
     * Method to check if given user is currently describing player.
     * @param   user user
     * @return  true is he is, false if not
     * @apiNote method name equals the one for teams to be called from ui with different types
     */
    public boolean isGuessingTeam(User user) {
        return currentRound == null ? false : currentRound.getGuessingUser().equals(user);
    }

    /**
     * Method to check if saved user is in current round's guessing team.
     * @return true if it is, false if not
     */
    public boolean isInGuessingTeam() {
        return currentRound == null ? false : isInTeam(currentRound.getGuessingTeam(), user);
    }

    /**
     * Method to check if saved user is in last round's guessing team.
     * @return true if saved user is, false if not
     */
    public boolean isInLastGuessingTeam() {
        return lastRound == null ? false : isInTeam(lastRound.getGuessingTeam(), user);
    }

    /**
     * Method to check if saved user is in given team.
     * @param  team team
     * @return true if it is, false if not
     */
    public boolean isUserTeam(Team team) {
        return isInTeam(team, user);
    }

    /**
     * Method to check if saved user is in same team as given user.
     * @param   user user
     * @return  true if it is, false if not
     * @apiNote method name equals the one for teams to be called from ui with different types
     */
    public boolean isUserTeam(User user) {
        return getCurrentTeam() == null ? false : currentTeam.getTeamMembers().contains(user);
    }

    /**
     * Method to get the formatted current countdown value for the saved user's current game.
     * @return formatted countdown value
     */
    public String getCountDown() {
        return gameManagerController.getCountDownForGame(currentGame);
    }

    /**
     * Method to get the numeric current countdown value for the saved user's current game.
     * @return numeric countdown value
     */
    public int getTimer() {
        return gameManagerController.getTimerForGame(currentGame);
    }

    /**
     * Returns the display state of the current game.
     * @return state of current game
     */
    public RunState getCurrentRunState() {
        if (getCurrentGame() == null) return RunState.NONE;

        switch (currentGame.getStatus()) {
            case FINISHED:
            case CANCELED:
            case SETUP:
                return RunState.NONE;

            case VALID_SETUP:
            case HALTED:
                return RunState.WAITING;

            default:
                return RunState.RUNNING;
        }
    }

    /**
     * Returns the current round state for the current game, if it is in {@link RunState#RUNNING}.
     * @return state of current round
     */
    public RoundState getCurrentRoundState() {
        return getCurrentRunState() == RunState.RUNNING ?
            gameManagerController.getRoundStateForGame(currentGame) : RoundState.NONE;
    }

    /**
     * Returns the current reason to wait for the current game, if it is in {@link RunState#WAITING}.
     * @return state of current game
     */
    public WaitReason getCurrentWaitReason() {
        return getCurrentRunState() == RunState.WAITING ?
            gameManagerController.getWaitReasonForGame(currentGame) : WaitReason.NONE;
    }

    /**
     * Returns the current reason for no game for the current game, if it is in {@link RunState#NONE}.
     * @return state of current game
     */
    public NoGameReason getCurrentNoGameReason() {
        return getCurrentRunState() == RunState.NONE ?
            gameManagerController.getNoGameReasonForGame(currentGame) : NoGameReason.NONE;
    }

    /**
     * Checks whether a round is currently running, idependent of other states.
     * @return true if a round is currently running, false otherwise
     */
    public boolean isRoundRunning() {
        return getCurrentGame() != null && gameManagerController.getRoundStateForGame(currentGame) == RoundState.RUNNING;
    }

    /**
     * Method that sets the attributes for the current game state depending on the set user.
     */
    public void setup() {
        updateRound();
    }

    /**
     * Method that sets the attributes when the game is paused.
     */
    public void pauseGame() {}

    /**
     * Method that sets the attributes when the game has finished or was canceled.
     */
    public void gameOver() {
        destroy();
    }

    /**
     * Method that sets the attributes when a new round starts.
     */
    public void startRound() {
        updateRound();
    }

    /**
     * Method that sets the attributes when a round ends by cube.
     */
    public void endRound() {
        updateRound();
    }

    /**
     * Method that sets the attributes when a round ends by count-down.
     */
    public void endRoundViaCountDown() {
        updateRound();
        incorrectRound();
    }

    /**
     * Method that sets the attributes when a round is validated and the next one is starting.
     */
    public void validatedRound() {
        updateRound();
    }

    /**
     * Method to validate a round as correct. Gamemanagercontroller saves round correct.
     */
    public void correctRound() {
        gameManagerController.validateRoundOfGame(getCurrentGame(), Validation.CORRECT);
    }

    /**
     * Method to validate a round as incorrect. Gamemanagercontroller saves round incorrect.
     */
    public void incorrectRound() {
        gameManagerController.validateRoundOfGame(getCurrentGame(), Validation.INCORRECT);
    }

    /**
     * Method to validate a round as cheated. Gamemanagercontroller saves round cheated.
     */
    public void cheatedRound() {
        gameManagerController.validateRoundOfGame(getCurrentGame(), Validation.CHEATED);
    }

    /**
     * Method to calculate the points a certain team has reached in the current game
     * @param  team of which points are to be estimated
     * @return points
     */
    public Integer calculatePointsOfTeam(Team team) {
        return gameManagerController.getPointsOfTeamForGame(currentGame, team);
    }

    /**
     * Method to check which team has won the current game
     * @return winning team
     */
    public Team computeWinningTeam() {
        return gameManagerController.getTeamWithMostPointsForGame(currentGame);
    }

    public List<TeamScore> computeFinalRanking() {
        return highscoreService.getTeamRanking(currentGame);
    }

    private boolean isInTeam(Team team, User user) {
        return team == null ? false : team.getTeamMembers().contains(user);
    }

    private void updateRound() {
        switch (getCurrentRoundState()) {
            case STARTING:
            case RUNNING:
            case VALIDATING:
                Round curr = gameManagerController.getCurrentRoundForGame(currentGame);
                if (!(curr == null || curr.equals(currentRound))) lastRound = currentRound;
                currentRound = curr;
                break;

            default:
                break;
        }
    }

    private void destroy() {
        currentGame = null;
        currentTeam = null;
        currentRound = null;
        lastRound = null;
    }
}
