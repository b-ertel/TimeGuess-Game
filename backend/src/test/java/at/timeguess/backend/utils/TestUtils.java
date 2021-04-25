package at.timeguess.backend.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;

public class TestUtils {

    /**
     * Maps user roles to numbers
     */
    public final static Map<Integer, UserRole> USERROLES;

    /**
     * List of arguments for parameterized tests: user + map of pairs topic/count
     * To be extracted by a separate method producing a stream for JUnit5
     */
    public final static List<Arguments> USER_TOTALWINSBYTOPIC;

    static {
        USERROLES = new HashMap<>();
        USERROLES.put(1, UserRole.ADMIN);
        USERROLES.put(2, UserRole.MANAGER);
        USERROLES.put(3, UserRole.PLAYER);

        USER_TOTALWINSBYTOPIC = List.of(
            Arguments.of("admin", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("user1", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 1 } })),
            Arguments.of("user2", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 1 }, { "STAR WARS", 0 } })),
            Arguments.of("elvis", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("michael", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 1 }, { "STAR WARS", 1 } })),
            Arguments.of("felix", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 1 } })),
            Arguments.of("lorenz", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 1 }, { "STAR WARS", 0 } })),
            Arguments.of("verena", toMap(new Object[][] { { "GEOGRAPHY", 1 }, { "MOVIES", 0 }, { "STAR WARS", 0 } })),
            Arguments.of("claudia", toMap(new Object[][] { { "GEOGRAPHY", 1 }, { "MOVIES", 1 }, { "STAR WARS", 0 } })),
            Arguments.of("clemens", toMap(new Object[][] { { "GEOGRAPHY", 0 }, { "MOVIES", 0 }, { "STAR WARS", 1 } })));
    }

    /**
     * Creates a simple user with the given username only
     * @param username
     * @return
     */
    public static User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    /**
     * Creates a simple user with the given id only
     * @param username
     * @return
     */
    public static User createUser(Long userid) {
        User user = new User();
        user.setId(userid);
        return user;
    }

    public static List<User> createUsers(int count) {
        List<User> list = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            list.add(TestUtils.createUser((i + 1) * 10));
        }
        return list;
    }

    /**
     * Asserts the given list.
     * @param <T>          element type of the given list and the functor
     * @param result       list to assert, must not be null
     * @param errMsg       message to print for each element in the list not passing the functor call
     * @param expectResult true if the list is expected to contain elements, false if not
     * @param checker      a functor checking each element in the list
     */
    public static <T> void assertResultList(Collection<T> result, String errMsg, boolean expectResult,
            Function<T, Boolean> checker) {
        assertNotNull(result, "resulting list must not be null");
        assertTrue(expectResult ? result.size() > 0 : result.size() == 0, "was " + (expectResult ? "" : "not") + " expecting a result");

        for (T elem : result) {
            assertTrue(checker.apply(elem), String.format(errMsg, elem));
        }
    }

    private static Map<String, Integer> toMap(Object[][] values) {
        return Stream.of(values).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));
    }

}
