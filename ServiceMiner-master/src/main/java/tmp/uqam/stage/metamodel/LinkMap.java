package tmp.uqam.stage.metamodel;

import tmp.uqam.stage.metamodel.jsonserializer.Relation;
import tmp.uqam.stage.metamodel.jsonserializer.RelationType;

import java.util.*;

/**
 * A map structure to store a link and its dependencies kind and count
 */
public class LinkMap {

    private Map<Link, Map<LinkType, Integer>> linkListMap;

    public LinkMap() {
        this.linkListMap = new HashMap<>();
    }

    /**
     * Add a linktype to a link,
     * it will add the link if it doesn't exist, else it will be added to the link
     * or added to the count of this linktype for the link
     */
    public void put(Link key, LinkType item) {
        Map<LinkType, Integer> map = linkListMap.get(key);
        if (map == null) {
            map = new EnumMap<>(LinkType.class);
            map.put(item, 1);
            linkListMap.put(key, map);
        } else {
            map.merge(item, 1, (a, b) -> a + b);
        }
    }

    /**
     * Simplify the map as to flatten the linktype into just the weight computed from their
     * sums and weights
     */
    public Map<Link, Double> migrateAndComputeLinks() {
        Map<Link, Double> newLinks = new HashMap<>();
        linkListMap.forEach((link, map) ->
                newLinks.put(link, map.keySet()
                        .stream()
                        .mapToDouble(linkType -> linkType.getWeight() * map.get(linkType))
                        .sum())
        );
        return newLinks;
    }

    public List<Relation> toSCRelations() {
        List<Relation> relations = new ArrayList<>();
        linkListMap.forEach((link, map) -> {
            Set<LinkType> linkTypes = map.keySet();
            if (linkTypes.contains(LinkType.ONEWAY)) {
                relations.add(new Relation(link.getFrom().getName(), link.getTo().getName(), RelationType.AGGREGATION));
            }
            if (linkTypes.contains(LinkType.EXTENDS) || linkTypes.contains(LinkType.IMPLEMENTS)) {
                relations.add(new Relation(link.getFrom().getName(), link.getTo().getName(), RelationType.INHERITANCE));
            }
        });
        return relations;
    }

    public int size() {
        return linkListMap.size();
    }

    ///////////////TEST/////////////////

    public Map<Link, Map<LinkType, Integer>> getMap() {
        return linkListMap;
    }
}
