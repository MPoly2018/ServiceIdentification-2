package tmp.uqam.stage.MinSpanningTree;

//import ch.uzh.ifi.seal.monolith2microservices.models.graph.Component;

import java.util.Comparator;


public class ComponentComparator implements Comparator<Component> {

    @Override
    public int compare(Component o1, Component o2) {
        return new Integer(o1.getSize()).compareTo(o2.getSize());
    }
}
