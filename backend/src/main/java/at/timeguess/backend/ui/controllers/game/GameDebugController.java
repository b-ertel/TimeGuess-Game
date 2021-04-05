package at.timeguess.backend.ui.controllers.game;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.services.GameService;

@Component
@PreAuthorize("isAuthenticated()")
@Scope("view")
public class GameDebugController {

    @Autowired
    private GameService gameService;

    public Collection<Game> getGames() {
        return gameService.getAllGames();
    }
}
