package at.timeguess.backend.model;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static at.timeguess.backend.utils.TestSetup.createTeam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GameTeam} and {@link GameTeamId}.
 */
public class GameTeamTest {

    @Test
    public void testGameTeam() {
        long expectedGameId = 5L, expectedTeamId = 15L;

        Game game = createGame(expectedGameId);
        Team team = createTeam(expectedTeamId);
        GameTeam gt = new GameTeam(game, team);
        assertFalse(gt.isNew());
        assertEquals(0, gt.getPoints());

        GameTeamId expectedGT = new GameTeamId(expectedGameId, expectedTeamId);
        assertEquals(expectedGT, gt.getId());

        expectedGT.setGameId(expectedGameId + 1);
        expectedGT.setTeamId(expectedTeamId + 1);
        assertNotEquals(expectedGT, gt.getId());
    }
}
