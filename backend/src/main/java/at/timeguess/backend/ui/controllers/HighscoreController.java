package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.utils.UserScores;
import at.timeguess.backend.services.HighscoreService;

/**
 * Controller for the game results view.
 */
@Component
@Scope("view")
public class HighscoreController {

    @Autowired
    HighscoreService highscoreService;

    public List<UserScores> getHighscoresOnGamesWon() {
        return highscoreService.getHighscoresByGamesWon();
    }

}
