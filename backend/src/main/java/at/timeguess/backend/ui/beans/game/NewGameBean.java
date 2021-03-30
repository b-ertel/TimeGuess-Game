package at.timeguess.backend.ui.beans.game;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.game.GameState;
import at.timeguess.backend.services.GameService;

@Component
@Scope("session")
public class NewGameBean implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -4340754545184734831L;
	@Autowired
	private GameService gameService;

	private String gameName;
	private int maxPoints = 10;

	public void create() {
		Game game = new Game();
		game.setName(gameName);
		game.setMaxPoints(maxPoints);

		game.setStatus(false);
		game.setState(GameState.SETUP);

		gameService.saveGame(game);

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

}
