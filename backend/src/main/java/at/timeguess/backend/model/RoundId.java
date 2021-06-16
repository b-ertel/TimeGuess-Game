package at.timeguess.backend.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * Entity representing a round in a game.
 */
@Embeddable
public class RoundId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long gameId;
    private Integer nr;

    public RoundId() {}

    public RoundId(Long gameId, Integer nr) {
        this.gameId = gameId;
        this.nr = nr;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getNr() {
        return nr;
    }

    public void setNr(Integer nr) {
        this.nr = nr;
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 7;
        result = prime * result + (gameId == null ? 0 : gameId.hashCode());
        result = prime * result + (nr == null ? 0 : nr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        final RoundId other = (RoundId) obj;
        return Objects.equals(getGameId(), other.getGameId()) && Objects.equals(getNr(), other.getNr());
    }
}
