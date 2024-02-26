package tmp.uqam.stage.slicing;

/**
 * Enumeration for the classtype as well as their associate shape for the graphical representation
 */
public enum ClassType {

    INTERFACE("ellipse"),
    ABSTRACT_CLASS("ellipse"),
    CLASS("box"),
    ENUM("box");

    private final String shape;

    ClassType(String shape) {
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }
}
