package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.repositories.GameTeamRepository;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Service for accessing and manipulating {@link GameTeam} data.
 */
@Component
@Scope("application")
@CDIContextRelated
public class GameTeamService {

    @Autowired
    private GameTeamRepository gameTeamRepository;

    @Autowired
    private RoundService roundService;

    /**
     * Saves the points reached for all teams in the given game.
     * @param   game the game to save points reached
     */
    public void updatePoints(Game game) {
        List<GameTeam> list = gameTeamRepository.findByGame(game);
        list.stream().forEach(gt -> {
            gt.setPoints(roundService.getPointsOfTeamInGame(game, gt.getTeam()));
            gameTeamRepository.save(gt);
        });
    }
}
