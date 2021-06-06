package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.model.utils.TeamScore;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.HighscoreService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.beans.SessionInfoBean;
import at.timeguess.backend.ui.controllers.GameManagerController.NoGameReason;
import at.timeguess.backend.ui.controllers.GameManagerController.RoundState;
import at.timeguess.backend.ui.controllers.GameManagerController.WaitReason;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * This controller holds the game-content of the logged-in user.
 */
@Controller
@Scope("session")
public class GameRoundController {

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
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private GameManagerController gameManagerController;
    @Autowired
    private CountDownController countDownController;
    @Autowired
    private RoundService roundService;
    @Autowired
    private GameLogicService gameLogic;
    @Autowired
    private HighscoreService highscoreService;

    private Round currentRound;

    private Round nextRound;

    private Game currentGame = null;

    private boolean inRound = false;

    private boolean inGuessingTeam = false;

    /**
     * Method that sets the attributes of class depending on which user is logged in
     */
    public void setup() {
        System.out.println("Setup " + sessionInfoBean.getCurrentUserName());
        this.nextRound = gameManagerController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
        this.inGuessingTeam = nextRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
        this.currentGame = this.gameManagerController.getCurrentGameForUser(this.sessionInfoBean.getCurrentUser());
    }

    /**
     * Method that sets all important attributes to null
     */
    public void destroy() {
        System.out.println("Destroy " + sessionInfoBean.getCurrentUserName());
        this.currentGame = null;
        this.currentRound = null;
        this.nextRound = null;
    }

    /**
     * Method to get round that is to be played
     */
    public void getNextRoundInfos() {
        this.nextRound = gameManagerController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
        this.inGuessingTeam = nextRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
    }

    /**
     * Method to start a new round, including count-down
     */
    public void startRound() {
        this.inRound = true;
        this.currentRound = gameManagerController.getCurrentRoundForUser(sessionInfoBean.getCurrentUser());
        this.countDownController.startCountDown(currentRound.getTime(), sessionInfoBean.getCurrentUser());
        this.inGuessingTeam = currentRound.getGuessingTeam().getTeamMembers().contains(sessionInfoBean.getCurrentUser());
    }

    /**
     * Method to end a round and the count-down
     */
    public void endRound() {
        this.countDownController.endCountDown();
        setInRound(false);
    }

    /**
     * Method to end a round through count-down
     */
    public void endRoundViaCountDown() {
        gameManagerController.endRoundByCountDown(sessionInfoBean.getCurrentUser());
        incorrectRound();
    }

    public Round getCurrentRound() {
        return this.currentRound;
    }

    public Round getNextRound() {
        return this.nextRound;
    }

    public void setInRound(boolean inRound) {
        this.inRound = inRound;
    }

    public boolean getInRound() {
        return this.inRound;
    }

    public void setInGuessingTeam(boolean bool) {
        this.inGuessingTeam = bool;
    }

    public boolean isInGuessingTeam() {
        return inGuessingTeam;
    }

    /**
     * Method to validate a round as correct. Gamemanagercontroller saves round correct.
     */
    public void correctRound() {
        Game game = gameManagerController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
        gameManagerController.validateRoundOfGame(game, Validation.CORRECT);
    }

    /**
     * Method to validate a round as incorrect. Gamemanagercontroller saves round incorrect.
     */
    public void incorrectRound() {
        Game game = gameManagerController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
        gameManagerController.validateRoundOfGame(game, Validation.INCORRECT);
    }

    /**
     * Method to validate a round as cheated. Gamemanagercontroller saves round cheated.
     */
    public void cheatedRound() {
        Game game = gameManagerController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
        gameManagerController.validateRoundOfGame(game, Validation.CHEATED);
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Method to get a list of all teams in the current game
     * @return list of teams
     */
    public List<Team> getTeamsInGame() {
        return List.copyOf(currentGame.getTeams());
    }

    /**
     * Returns the display state of the current game.
     * @return
     */
    public RunState getCurrentRunState() {
        if (getCurrGame() == null) return RunState.NONE;

        switch (currGame.getStatus()) {
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
     * Returns the state of the current round if {@link run} states for a current round in any running game
     * @return
     */
    public RoundState getCurrentRoundState() {
        return getCurrentRunState() == RunState.RUNNING ?
            gameManagerController.getRoundStateForGame(currGame) : RoundState.NONE;
    }

    public WaitReason getCurrentWaitReason() {
        return getCurrentRunState() == RunState.WAITING ?
            gameManagerController.getWaitReasonForGame(currGame) : WaitReason.NONE;
    }

    public NoGameReason getCurrentNoGameReason() {
        return getCurrentRunState() == RunState.NONE ?
            gameManagerController.getNoGameReasonForGame(currGame) : NoGameReason.NONE;
    }
    private Game currGame;
    private Game getCurrGame() {
        if (currGame == null) {
            currGame = gameManagerController.getCurrentGameForUser(sessionInfoBean.getCurrentUser());
        }
        return currGame;
    }

    /**
     * Method to calculate the points a certain team has reached in the current game
     * @param  team of which points are to be estimated
     * @return points
     */
    public Integer calculatePointsOfTeam(Team team) {
        return roundService.getPointsOfTeamInGame(currentGame, team);
    }

    /**
     * Method to check which team has won the current game
     * @return winning team
     */
    public Team computeWinningTeam() {
        Team winningTeam = gameLogic.getTeamWithMostPoints(currentGame);
        return winningTeam;
    }

    public List<TeamScore> computeFinalRanking() {
        List<TeamScore> ls = highscoreService.getTeamRanking(currentGame);
        return ls;
    }
}
