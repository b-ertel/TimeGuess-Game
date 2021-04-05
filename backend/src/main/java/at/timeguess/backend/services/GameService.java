package at.timeguess.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.model.*;

@Component
@Scope("application")
public class GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private GameRepository gameRepo;

    public Collection<Game> getAllGames() {

        return gameRepo.findAll();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    public Collection<Game> getByStatus(GameState gs) {
        return gameRepo.findByStatus(gs);
    }

    @PreAuthorize("hasAuthority('MANAGER') ")
    public Game loadGame(Long gameId) {
        return gameRepo.findById(gameId);
    }

    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public Game saveGame(Game g) {
        // TODO: confirm validity
        LOGGER.info("Saving game with id {} started.", g.getId());
        Game g2 = gameRepo.save(g);
        LOGGER.info("Saving game with id {} done.", g.getId());
        return g2;
    }

    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public void deleteGame(Game g) {
        gameRepo.delete(g);
    }
}
