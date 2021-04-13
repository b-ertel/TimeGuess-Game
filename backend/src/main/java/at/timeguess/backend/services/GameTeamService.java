package at.timeguess.backend.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.repositories.GameTeamRepository;

@Component
@Scope("application")
public class GameTeamService {
    @Autowired
    private GameTeamRepository gameTeamRepo;

    public Collection<GameTeam> getAllGameTeams() {
        return gameTeamRepo.findAll();
    }

    public GameTeam save(GameTeam gameTeam) {
        GameTeam ret = gameTeamRepo.save(gameTeam);
        return ret;
    }

    public Object delete(GameTeam t) {
        gameTeamRepo.delete(t);
        return t;
    }
}
