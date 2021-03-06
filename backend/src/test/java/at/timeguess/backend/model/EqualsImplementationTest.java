package at.timeguess.backend.model;

import static at.timeguess.backend.utils.TestSetup.*;

import javax.swing.Timer;

import static at.timeguess.backend.ui.controllers.GameManagerController.GameData;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the contract.
 * See {@linkplain http://www.jqno.nl/equalsverifier/} for more information.
 */
public class EqualsImplementationTest {

    @Test
    public void testCubeEqualsContract() {
        Cube cube1 = createCube(1L);
        Cube cube2 = createCube(2L);
        Configuration cfg1 = createConfig(1L);
        Configuration cfg2 = createConfig(2L);
        EqualsVerifier.forClass(Cube.class).withPrefabValues(Cube.class, cube1, cube2)
            .withPrefabValues(Configuration.class, cfg1, cfg2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testGameEqualsContract() {
        Game game1 = createGame(1L);
        Game game2 = createGame(2L);
        Topic topic1 = createTopic(1L);
        Topic topic2 = createTopic(2L);
        Team team1 = createTeam(1L);
        Team team2 = createTeam(2L);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        Round round1 = createRound(1);
        Round round2 = createRound(2);
        Cube cube1 = createCube(1L);
        Cube cube2 = createCube(2L);
        EqualsVerifier.forClass(Game.class).withPrefabValues(Game.class, game1, game2)
            .withPrefabValues(Topic.class, topic1, topic2)
            .withPrefabValues(Team.class, team1, team2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Round.class, round1, round2)
            .withPrefabValues(Cube.class, cube1, cube2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testGameTeamEqualsContract() {
        GameTeamId gt1 = new GameTeamId(1L, 2L);
        GameTeamId gt2 = new GameTeamId(2L, 1L);
        Game game1 = createGame(1L);
        Game game2 = createGame(2L);
        Team team1 = createTeam(1L);
        Team team2 = createTeam(2L);
        EqualsVerifier.forClass(GameTeamId.class).withPrefabValues(GameTeamId.class, gt1, gt2)
            .withPrefabValues(Game.class, game1, game2)
            .withPrefabValues(Team.class, team1, team2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testRoundEqualsContract() {
        Game game1 = createGame(1L);
        Game game2 = createGame(2L);
        Round round1 = createRound(1, game1);
        Round round2 = createRound(2, game2);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        Team team1 = createTeam(1L);
        Team team2 = createTeam(2L);
        Term term1 = createTerm(1L);
        Term term2 = createTerm(2L);
        EqualsVerifier.forClass(Round.class).withPrefabValues(Round.class, round1, round2)
            .withPrefabValues(Game.class, game1, game2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Team.class, team1, team2)
            .withPrefabValues(Term.class, term1, term2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testTeamEqualsContract() {
        Team team1 = createTeam(1L);
        Team team2 = createTeam(2L);
        Game game1 = createGame(1L);
        Game game2 = createGame(2L);
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        EqualsVerifier.forClass(Team.class).withPrefabValues(Team.class, team1, team2)
            .withPrefabValues(Game.class, game1, game2)
            .withPrefabValues(User.class, user1, user2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testTermEqualsContract() {
        Term term1 = createTerm(1L);
        Term term2 = createTerm(2L);
        Topic topic1 = createTopic(1L);
        Topic topic2 = createTopic(2L);
        EqualsVerifier.simple().forClass(Term.class).withPrefabValues(Term.class, term1, term2)
            .withPrefabValues(Topic.class, topic1, topic2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testTopicEqualsContract() {
        Topic topic1 = createTopic(1L);
        Topic topic2 = createTopic(2L);
        EqualsVerifier.forClass(Topic.class).withPrefabValues(Topic.class, topic1, topic2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testUserEqualsContract() {
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        Team team1 = createTeam(1L);
        Team team2 = createTeam(2L);
        Game game1 = createGame(1L);
        Game game2 = createGame(2L);
        EqualsVerifier.forClass(User.class).withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Team.class, team1, team2)
            .withPrefabValues(Game.class, game1, game2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testActivityEqualsContract() {
        EqualsVerifier.forClass(Activity.class).verify();
    }

    @Test
    public void testCubeStatusEqualsContract() {
        EqualsVerifier.forClass(CubeStatus.class).verify();
    }

    @Test
    public void testGameStateEqualsContract() {
        EqualsVerifier.forClass(GameState.class).verify();
    }

    @Test
    public void testUserRoleEqualsContract() {
        EqualsVerifier.forClass(UserRole.class).verify();
    }

    @Test
    public void testGameDataEqualsContract() {
        Game game1 = createGame(1L);
        Game game2 = createGame(2L);
        GameData gameData1 = new GameData(game1, null);
        GameData gameData2 = new GameData(game2, null);
        Round round1 = createRound(1);
        Round round2 = createRound(2);
        Timer timer1 = new Timer(10, e -> {});
        Timer timer2 = new Timer(11, e -> {});
        EqualsVerifier.forClass(GameData.class).withPrefabValues(GameData.class, gameData1, gameData2)
            .withPrefabValues(Game.class, game1, game2)
            .withPrefabValues(Round.class, round1, round2)
            .withPrefabValues(Timer.class, timer1, timer2)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }
}
