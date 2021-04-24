package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.TeamState;

public interface TeamRepository extends AbstractRepository<Team, Long> {
    /**
     * to find GameTeams via GameTeamState
     * @param state
     * @return
     */
    @Query("SELECT g FROM Team g WHERE :state =  g.state")
    List<Team> findByStatus(@Param("state") TeamState state);
}
