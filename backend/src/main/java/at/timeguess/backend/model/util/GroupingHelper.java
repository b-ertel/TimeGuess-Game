package at.timeguess.backend.model.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;

/**
 * Helper class used to combine JPA queries for final result retrieval.
 * @see {@link at.timeguess.backend.services.UserService.getTotalGamesWon} for usage example.
 */
@Entity
@Immutable
public class GroupingHelper {

    @Id
    private Long id;
    @Column
    private String name;
    @Column
    private Long count;

    public GroupingHelper() {
    }

    public GroupingHelper(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public GroupingHelper(Long id, Integer count) {
        this.id = id;
        this.count = count.longValue();
    }

    public GroupingHelper(Long id, String name, Long count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return String.format("id=%d, name=%s, count=%d", id, name, count);
    }

    public static class List {

        private java.util.List<GroupingHelper> list;

        public List(java.util.List<GroupingHelper> list) {
            this.list = list;
        }

        /**
         * Returns the Ids of the contained instances as Set.
         * @return A Set of Ids.
         */
        public java.util.Set<Long> getIds() {
            return list.stream().map(GroupingHelper::getId).collect(Collectors.toSet());
        }

        /**
         * Sums up the Counts of the intersecting Ids.
         * @param ids A list to intersect with the contained instances Ids.
         * @return A number representing the summed up value of the intersecting instances Counts.
         */
        public int getSumForIds(java.util.List<Long> ids) {
            return (int) list.stream().filter(gh -> ids.contains(gh.getId())).mapToLong(GroupingHelper::getCount).sum();
        }

        /**
         * Sums up the Counts of the intersecting Ids.
         * @param ids A list to intersect with the contained instances Ids.
         * @return A number representing the summed up value of the intersecting instances Counts.
         */
        public Map<String, Integer> getSumForIdsGroupedByName(java.util.List<Long> ids) {
            // create map with all names
            Map<String, Integer> ret = new HashMap<>(list.size());
            list.stream().forEach(gh -> ret.putIfAbsent(gh.getName(), 0));
            // sum counts for intersecting ids by names
            list.stream().forEach(iwc -> {
                if (ids.contains(iwc.getId())) {
                    String name = iwc.getName();
                    int ct = ret.get(name);
                    ret.put(name, (int) (ct + iwc.getCount()));
                }
            });
            return ret;
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder();
            list.stream().forEach(gh -> {
                ret.append(gh);
                ret.append('\n');
            });
            return ret.toString();
        }
    }
}
