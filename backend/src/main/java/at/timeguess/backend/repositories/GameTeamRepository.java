package at.timeguess.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.GameTeamId;
import at.timeguess.backend.model.TeamState;

public interface GameTeamRepository extends AbstractRepository<GameTeam, GameTeamId>{


}
