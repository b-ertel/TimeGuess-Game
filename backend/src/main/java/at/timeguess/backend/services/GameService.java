package at.timeguess.backend.services;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.model.*;
import at.timeguess.backend.model.GameState;

@Component
@Scope("application")
public class GameService {
	@Autowired
    private GameRepository gameRepo;
   
    @PreAuthorize("hasAuthority('USER')")
    public Collection<Game> getAllGames() {
        return gameRepo.findAll();
        }
    @PreAuthorize("hasAuthority('MANAGER')")
    public Collection<Game> getByStatus(GameState gs){
    	return gameRepo.findByStatus(gs);
    }
    
    @PreAuthorize("hasAuthority('MANAGER') ")
    public Game loadGame(Long gameId) {
        return gameRepo.findById(gameId);
    }
    @PreAuthorize("hasAuthority('MANAGER')")
    public Game saveGame(Game g) {
      // TODO: confirm validity
        return gameRepo.save(g);
    }
    @PreAuthorize("hasAuthority('MANAGER')")
    public void deleteGame(Game g) {
        gameRepo.delete(g);
    }
}
