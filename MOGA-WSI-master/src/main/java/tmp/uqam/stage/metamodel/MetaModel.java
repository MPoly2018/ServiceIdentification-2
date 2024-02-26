package tmp.uqam.stage.metamodel;

import tmp.uqam.stage.slicing.ClassVertex;

import java.util.Map;
import java.util.Set;

/**
 * Metamodel that stores all the class vertices as well as the link between them and their weights
 */
public class MetaModel {

    private Map<Link, Double> linkWeights;
    private Set<ClassVertex> classes;

    public MetaModel(Map<Link, Double> linkWeights, Set<ClassVertex> classes) {
        this.linkWeights = linkWeights;
        this.classes = classes;
    }

    public Map<Link, Double> getLinkWeights() {
        return linkWeights;
    }

    public Set<ClassVertex> getClasses() {
        return classes;
    }
}
