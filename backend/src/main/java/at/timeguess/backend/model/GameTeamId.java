package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class GameTeamId implements Serializable {
	 
    private static final long serialVersionUID = 1L;
 
    private Long gameId;
    private Long teamId;
 
    public GameTeamId() {}
 
    public GameTeamId(Long gameId, Long teamId) {
        super();
        this.gameId = gameId;
        this.teamId = teamId;
    }
 
    public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
 
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 5;
        result = prime * result + (gameId == null ? 0 : gameId.hashCode());
        result = prime * result + (teamId == null ? 0 : teamId.hashCode());
        return result;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
        	return true;
        
        if (obj == null || getClass() != obj.getClass())
            return false;
        
        final GameTeamId other = (GameTeamId)obj;
        return Objects.equals(getGameId(), other.getGameId()) && Objects.equals(getTeamId(), other.getTeamId());
    }
}
