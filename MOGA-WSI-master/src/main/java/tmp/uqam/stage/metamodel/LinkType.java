package tmp.uqam.stage.metamodel;

/**
 * enum for the different kinds of links between classes, weights are arbitrary
 */
public enum LinkType {

    EXTENDS(1.0),
    IMPLEMENTS(1.0),
    ONEWAY(1.5),
    METHOD_INVOKE(0.2),
    CONSTRUCTOR_INVOKE(0.3);

    private double weight;

    LinkType(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
