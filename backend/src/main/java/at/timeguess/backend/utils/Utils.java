package at.timeguess.backend.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {

    /**
     * Adds the given entry to the set saved in given map for given key, creating a new set if necessary.
     * @param <K>
     * @param <V>
     * @param map
     * @param key
     * @param entry
     */
    public static <K, V> void addToSet(Map<K, Set<V>> map, K key, V entry) {
        if (map != null && key != null && entry != null) {
            Set<V> entries = map.get(key);
            if (entries == null) {
                entries = new HashSet<>();
                entries.add(entry);
                map.put(key, entries);
            }
            else entries.add(entry);
        }
    }

    /**
     * Removes the given entry from the set in given map, doing null-checks where necessary.
     * @param <K>
     * @param <V>
     * @param map
     * @param key
     * @param entry
     */
    public static <K, V> void removeFromSet(Map<K, Set<V>> map, K key, V entry) {
        if (map != null && key != null && entry != null) {
            Set<V> entries = map.get(key);
            if (entries != null) entries.remove(entry);
        }
    }
}
