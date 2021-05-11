package at.timeguess.backend.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.params.provider.Arguments;

import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;

public class TestSetup {

    /**
     * Maps user roles to numbers
     */
    public final static Map<Integer, UserRole> USERROLES;

    /**
     * Maps game states to numbers
     */
    public final static Map<Integer, GameState> GAMESTATES;

    /**
     * List of arguments for parameterized tests: user + map of pairs topic/count To be extracted by a separate method
     * producing a stream for JUnit5
     */
    public final static List<Arguments> USER_TOTALWINSBYTOPIC;

    static {
        USERROLES = new HashMap<>();
        USERROLES.put(1, UserRole.ADMIN);
        USERROLES.put(2, UserRole.MANAGER);
        USERROLES.put(3, UserRole.PLAYER);

        GAMESTATES = new HashMap<>();
        GAMESTATES.put(0, GameState.SETUP);
        GAMESTATES.put(1, GameState.VALID_SETUP);
        GAMESTATES.put(2, GameState.PLAYED);
        GAMESTATES.put(3, GameState.HALTED);
        GAMESTATES.put(4, GameState.FINISHED);
        GAMESTATES.put(5, GameState.CANCELED);

        USER_TOTALWINSBYTOPIC = List.of(
            Arguments.of("admin",   toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("user1",   toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("user2",   toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 1 }, { "STAR WARS", 0 } })),
            Arguments.of("elvis",   toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("michael", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 1 }, { "STAR WARS", 1 } })),
            Arguments.of("felix",   toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 1 } })),
            Arguments.of("lorenz",  toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 1 }, { "STAR WARS", 0 } })),
            Arguments.of("verena",  toMap(new Object[][] { { "GEOGRAPHY", 1 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("claudia", toMap(new Object[][] { { "GEOGRAPHY", 1 }, { "MOVIES", 1 }, { "STAR WARS", 0 } })),
            Arguments.of("clemens", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })));
    }

    /**
     * Creates a simple configuration with the given id only.
     * @param configId
     * @return
     */
    public static Configuration createConfig(Long configId) {
        return createEntity(Configuration::new, e -> e.setId(configId));
    }

    /**
     * Creates a simple cube with the given id only.
     * @param cubeId
     * @return
     */
    public static Cube createCube(Long cubeId) {
        return createEntity(Cube::new, e -> e.setId(cubeId));
    }

    /**
     * Creates a simple cubeface with the given id only.
     * @param cubefaceId
     * @return
     */
    public static CubeFace createCubeFace(String cubefaceId) {
        return createEntity(CubeFace::new, e -> e.setId(cubefaceId));
    }

    /**
     * Creates a simple game with the given id only.
     * @param gameId
     * @return
     */
    public static Game createGame(Long gameId) {
        return createEntity(Game::new, e -> e.setId(gameId));
    }

    /**
     * Creates a simple round with the given id only.
     * @param roundId
     * @return
     */
    public static Round createRound(Long roundId) {
        return createEntity(Round::new, e -> e.setId(roundId));
    }

    /**
     * Creates a simple team with the given id only.
     * @param teamId
     * @return
     */
    public static Team createTeam(Long teamId) {
        return createEntity(Team::new, e -> e.setId(teamId));
    }

    /**
     * Creates a simple term with the given id only.
     * @param termId
     * @return
     */
    public static Term createTerm(Long termId) {
        return createEntity(Term::new, e -> e.setId(termId));
    }

    /**
     * Creates a simple topic with the given id only.
     * @param topicId
     * @return
     */
    public static Topic createTopic(Long topicId) {
        return createEntity(Topic::new, e -> e.setId(topicId));
    }

    /**
     * Creates a simple user with the given username only.
     * @param username
     * @return
     */
    public static User createUser(String username) {
        return createEntity(User::new, e -> e.setUsername(username));
    }

    /**
     * Creates a simple user with the given id only.
     * @param userId
     * @return
     */
    public static User createUser(Long userId) {
        return createEntity(User::new, e -> e.setId(userId));
    }

    /**
     * Creates a list of the given number of entities, constructing each with the given function and an arbitrary long
     * value.
     * @param count
     * @param createEntity
     * @return
     */
    public static <T> List<T> createEntities(Function<Long, T> createEntity, int count) {
        return LongStream.range(0, count).mapToObj(id -> createEntity.apply((id + 1) * 10)).collect(Collectors.toList());
    }

    /**
     * Creates a list of entities from the given D list, constructing each with the given function passing the D value.
     * @param createEntity
     * @param fromList
     * @return
     */
    public static <D, T> List<T> createEntities(Function<D, T> createEntity, Collection<D> from) {
        return createEntities(createEntity, from.stream());
    }

    /**
     * Creates a list of entities from the given D stream, constructing each with the given function passing the D value.
     * @param createEntity
     * @param fromList
     * @return
     */
    public static <D, T> List<T> createEntities(Function<D, T> createEntity, Stream<D> from) {
        return from.map(createEntity).collect(Collectors.toList());
    }

    /**
     * Creates a list of entities from the given string list (a colon separated string), constructing each with the
     * given function.
     * @param createEntity
     * @param colonSeparatedIntList
     * @return
     */
    public static <T> List<T> createEntities(Function<Long, T> createEntity, String colonSeparatedIntList) {
        return createList(colonSeparatedIntList).stream().map(createEntity).collect(Collectors.toList());
    }

    private static <T> T createEntity(Supplier<T> constructor, Consumer<T> keySetter) {
        T entity = constructor.get();
        keySetter.accept(entity);
        return entity;
    }

    private static List<Long> createList(String colonSeparatedIntList) {
        return Strings.isEmpty(colonSeparatedIntList) ?
            new ArrayList<>() :
            Stream.of(colonSeparatedIntList.split(";")).map(Long::valueOf).collect(Collectors.toList());
    }

    private static Map<String, Integer> toMap(Object[][] values) {
        return Stream.of(values).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));
    }
}
