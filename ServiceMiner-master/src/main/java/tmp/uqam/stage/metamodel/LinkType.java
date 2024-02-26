package tmp.uqam.stage.metamodel;

/**
 * enum for the different kinds of links between classes, weights are arbitrary
 */
public enum LinkType {

	
	/*
    EXTENDS(2.0),
    IMPLEMENTS(2.0),
    ONEWAY(1),
    METHOD_INVOKE(0.5),
    CONSTRUCTOR_INVOKE(0.5);
    
    */
	
	
	EXTENDS(4.0),
    IMPLEMENTS(4.0),
    ONEWAY(2),
    METHOD_INVOKE(1),
    CONSTRUCTOR_INVOKE(1);

    private double weight;

    LinkType(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
