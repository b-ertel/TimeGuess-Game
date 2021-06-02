package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static at.timeguess.backend.utils.TestSetup.createTeam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.GameRepository;

/**
 * Tests for {@link RoundService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class RoundServiceTest {

    @Autowired
    RoundService roundService;

    @Autowired
    GameRepository gameRepo;

    @Test
    public void getAllRounds() {
        assertEquals(19, roundService.getAllRounds().size());
    }

    @Test
    public void testRoundsPlayedinGame() {
        Game gameWithRounds = gameRepo.findById((long) 1).get();
        assertTrue(roundService.roundsPlayedInGame(gameWithRounds));

        Game gameWithoutRounds = gameRepo.findById((long) 6).get();
        assertFalse(roundService.roundsPlayedInGame(gameWithoutRounds));
    }

    @Test
    public void testRoundsPlayedinGameWithTeam() {
        Game gameWithRounds = gameRepo.findById((long) 1).get();
        Team teamWithRounds = gameWithRounds.getTeams().iterator().next();
        assertTrue(roundService.teamPlayedRoundsInGame(gameWithRounds, teamWithRounds));

        Game gameWithoutRounds = gameRepo.findById((long) 6).get();
        assertFalse(roundService.teamPlayedRoundsInGame(gameWithoutRounds, teamWithRounds));

        Team teamWithNoRoundsInGame = gameRepo.findById((long) 2).get().getTeams().iterator().next();
        assertFalse(roundService.teamPlayedRoundsInGame(gameWithoutRounds, teamWithNoRoundsInGame));
    }

    @Test
    public void testGetLastRound() {
        for (int i = 1; i < 5; i++) {
            Game game = gameRepo.findById((long) i).get();
            Round lastRound = roundService.getLastRound(game);
            assertEquals(lastRound.getId(), (long) 4 * i);
        }
        Game gameWithNoRound = gameRepo.findById((long) 6).get();
        assertNull(roundService.getLastRound(gameWithNoRound));
    }

    @Test
    public void testGetLastRoundOfTeam() {
        Game gameWithRounds = gameRepo.findById((long) 1).get();
        Team teamWithRounds = gameWithRounds.getTeams().iterator().next();
        assertEquals(teamWithRounds.getId(), roundService.getLastRoundOfTeam(gameWithRounds, teamWithRounds).getGuessingTeam().getId());

        Game gameWithoutRounds = gameRepo.findById((long) 6).get();
        assertNull(roundService.getLastRoundOfTeam(gameWithoutRounds, teamWithRounds));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|1|30", "1|2|40", "2|3|25", "2|4|50", "3|5|30", "3|6|50", "4|7|30", "4|8|35", "5|5|20", "5|7|20" })
    public void testGetPointsOfTeamInGame(final Long gameId, final Long teamId, final Integer expected) {
        Game game = createGame(gameId);
        Team team = createTeam(teamId);
        Integer result = roundService.getPointsOfTeamInGame(game, team);

        assertEquals(expected, result);
    }
}
