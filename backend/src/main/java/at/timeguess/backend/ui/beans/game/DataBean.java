package at.timeguess.backend.ui.beans.game;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;

import at.timeguess.backend.model.game.GameState;

@ManagedBean
@ApplicationScoped
public class DataBean {
	public GameState[] getStates() {
		return GameState.values();
	}
}
