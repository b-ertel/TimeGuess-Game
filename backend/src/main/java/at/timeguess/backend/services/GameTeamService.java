package at.timeguess.backend.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.GameTeamRepository;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Service for accessing and manipulating user data.
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
     * Saves the round.
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param   round the round to save
     * @return  the saved round
     * @apiNote Message handling ist done here, because this is the central place for saving terms.
     */
    public void updatePoints(Game game) {
        List<GameTeam> list = gameTeamRepository.findByGame(game);
        list.stream().forEach(gt -> {
		        gt.setPoints(roundService.getPointsOfTeamInGame(game, gt.getTeam()));
		        gameTeamRepository.save(gt);
		});
    }
}
