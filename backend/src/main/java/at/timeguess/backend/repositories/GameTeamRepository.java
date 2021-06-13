package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.GameTeamId;

public interface GameTeamRepository extends AbstractRepository<GameTeam, GameTeamId> {

    /**
     * to find GameTeams of a certain game
     * @param game game
     * @return list of {@link GameTeam}s
     */
    @Query("SELECT g FROM GameTeam g WHERE :game =  g.game")
    List<GameTeam> findByGame(@Param("game") Game game);
}
