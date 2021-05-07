package at.timeguess.backend.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.springframework.data.domain.Persistable;

@Entity
public class GameTeam implements Serializable, Persistable<GameTeamId>{

    private static final long serialVersionUID = 6862225865089517788L;

    @EmbeddedId
    private GameTeamId id;

    public GameTeam() {
        this.id = new GameTeamId();
    }

    public GameTeam(Game game, Team team) {
        this.id = new GameTeamId(game.getId(), team.getId());
        this.game = game;
        this.team = team;
    }

    @ManyToOne
    @MapsId("teamId")
    private Team team;

    @ManyToOne
    @MapsId("gameId")
    private Game game;

    private int points;


    public Team getTeam() {
        return team;
    }

    public Game getGame() {
        return game;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public boolean isNew() {
        return this.id == null ;
    }

    @Override
    public GameTeamId getId() {
        return id;
    }
}
