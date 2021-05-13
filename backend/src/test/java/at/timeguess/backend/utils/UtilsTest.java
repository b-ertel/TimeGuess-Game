package at.timeguess.backend.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Utils}.
 */
public class UtilsTest {

    private final String expectedKey = "key";
    private final List<Long> expectedValues = List.of(5L, 12L);
    private final Map<String, Set<Long>> expected = new HashMap<>();

    @BeforeEach
    public void emptyExpectedMap() {
        expected.clear();
    }

    @Test
    public void testAddToSet() {
        assertDoesNotThrow(() -> Utils.addToSet(null, null, null));

        assertDoesNotThrow(() -> Utils.addToSet(expected, null, null));
        assertEquals(0, expected.size());

        assertDoesNotThrow(() -> Utils.addToSet(expected, expectedKey, null));
        assertEquals(0, expected.size());

        assertDoesNotThrow(() -> Utils.addToSet(expected, expectedKey, expectedValues.get(0)));
        assertEquals(1, expected.size());
        assertEquals(Set.of(expectedValues.get(0)), expected.get(expectedKey));

        assertDoesNotThrow(() -> Utils.addToSet(expected, expectedKey, expectedValues.get(1)));
        assertEquals(1, expected.size());
        assertEquals(new HashSet<>(expectedValues), expected.get(expectedKey));
    }

    @Test
    public void testRemoveFromSet() {
        assertDoesNotThrow(() -> Utils.removeFromSet(null, null, null));

        assertDoesNotThrow(() -> Utils.removeFromSet(expected, null, null));
        assertEquals(0, expected.size());

        assertDoesNotThrow(() -> Utils.removeFromSet(expected, expectedKey, null));
        assertEquals(0, expected.size());

        assertDoesNotThrow(() -> Utils.removeFromSet(expected, expectedKey, expectedValues.get(0)));
        assertEquals(0, expected.size());

        expected.put(expectedKey, new HashSet<>(expectedValues));
        assertDoesNotThrow(() -> Utils.removeFromSet(expected, expectedKey, expectedValues.get(0)));
        assertEquals(1, expected.size());
        assertEquals(Set.of(expectedValues.get(1)), expected.get(expectedKey));

        assertDoesNotThrow(() -> Utils.removeFromSet(expected, expectedKey, expectedValues.get(1)));
        assertEquals(1, expected.size());
        assertEquals(new HashSet<>(), expected.get(expectedKey));
    }
}
