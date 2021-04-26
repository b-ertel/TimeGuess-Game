package at.timeguess.backend.ui.beans;

import java.io.Serializable;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.GameService;

/**
 * Bean for creating a new game.
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class NewGameBean implements Serializable {

    private static final long serialVersionUID = -4340754545184734831L;

    @Autowired
    private GameService gameService;

    @Autowired
    private MessageBean messageBean;

    private String gameName;
    private int maxPoints = 10;
    private Topic topic;

    public void createGame() {
        if (this.validInput()) {
            Game game = new Game();

            game.setName(gameName);
            game.setMaxPoints(maxPoints);
            game.setTopic(topic);
            game.setStatus(GameState.SETUP);

            gameService.saveGame(game);
        }
        else {
            messageBean.alertErrorFailValidation("Game creation failed", "Input fields are invalid");
        }
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @param gameName the gameName to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the maxPoints
     */
    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * @param maxPoints the maxPoints to set
     */
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    /**
     * @return the topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Clears all fields.
     */
    public void clearFields() {
        this.setGameName(null);
        this.setMaxPoints(0);
        this.setTopic(null);
    }

    /**
     * Checks if all fields contain valid values.
     * @return
     */
    public boolean validInput() {
        if (Strings.isBlank(getGameName())) return false;
        if (getMaxPoints() <= 0) return false;
        if (this.getTopic() == null) return false;
        return true;
    }
}
