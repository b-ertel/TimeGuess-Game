package at.timeguess.backend.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;

public interface GameRepository extends AbstractRepository<Game, Long> {

    /**
     * to find open games (GameState.VALID_SETUP), active (GameState.PLAYED),
     * finished (GameState.FINISHED)
     *
     * @param status
     * @return
     */
    @Query("SELECT g FROM Game g WHERE :status =  g.status")
    Collection<Game> findByStatus(@Param("status") GameState status);
    
}
