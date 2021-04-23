package at.timeguess.backend.tests;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import at.timeguess.backend.model.Activity;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the contract.
 * See {@linkplain http://www.jqno.nl/equalsverifier/} for more information.
 */
public class EqualsImplementationTest {

    @Test
    public void testUserEqualsContract() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setId(1L);
        User user2 = new User();
        user2.setUsername("user2");
        user2.setId(2L);
        Team team1 = new Team();
        Team team2 = new Team();
        team1.setId(1L);
        team2.setId(2L);
        Game game1 = new Game();
        game1.setName("game1");
        game1.setId(1L);
        Game game2 = new Game();
        game2.setName("game2");
        game2.setId(2L);
        EqualsVerifier.forClass(User.class).withPrefabValues(User.class, user1, user2)
                .withPrefabValues(Team.class, team1, team2).withPrefabValues(Game.class, game1, game2)
                .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testUserRoleEqualsContract() {
        EqualsVerifier.forClass(UserRole.class).verify();
    }

    @Test
    public void testActivityEqualsContract() {
        EqualsVerifier.forClass(Activity.class).verify();
    }

    @Test
    public void testTermEqualsContract() {
        Term newTerm1 = new Term();
        newTerm1.setName("Test Term 1");
        Term newTerm2 = new Term();
        newTerm2.setName("Test Term 2");
        Topic newTopic1 = new Topic();
        newTopic1.setName("Test Topic 1");
        Topic newTopic2 = new Topic();
        newTopic2.setName("Test Topic 2");
        EqualsVerifier.simple().forClass(Term.class).withPrefabValues(Term.class, newTerm1, newTerm2)
                .withPrefabValues(Topic.class, newTopic1, newTopic2)
                .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void testTopicEqualsContract() {
        Topic newTopic1 = new Topic();
        newTopic1.setName("Test Topic 1");
        Topic newTopic2 = new Topic();
        newTopic2.setName("Test Topic 2");
        EqualsVerifier.forClass(Topic.class).withPrefabValues(Topic.class, newTopic1, newTopic2)
                .suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }
}
