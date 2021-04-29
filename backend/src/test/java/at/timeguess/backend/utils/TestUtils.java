package at.timeguess.backend.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestUtils {

    /**
     * Asserts the given lists, comparing them using {@link Object#equals} on all contained items.
     * @param <T>
     * @param expected
     * @param result
     */
    public static <T> void assertLists(Collection<T> expected, Collection<T> result) {
        assertLists(expected, result, (r, e) -> Objects.equals(e, r));
    }

    /**
     * Asserts the given lists, comparing them using the {@link compareTo} function on all contained items.
     * @param <T>
     * @param expected
     * @param result
     */
    public static <T extends Comparable<T>> void assertListsCompare(Collection<T> expected, Collection<T> result) {
        assertLists(expected, result, (r, e) -> e.compareTo(r) == 0);
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

    private static <T> void assertLists(Collection<T> expected, Collection<T> result,
            BiFunction<T, T, Boolean> predicate) {
        int expSize = expected.size(), resSize = result.size();
        assertEquals(expSize, resSize, String.format("expected list size of %d but is %d", expSize, resSize));

        List<T> notInBoth = result.stream()
                .filter(r -> expected.stream().filter(e -> predicate.apply(e, r)).count() != 1)
                .collect(Collectors.toList());
        assertEquals(0, notInBoth.size(), "expected and result lists should correspond, but don't");
    }
}
