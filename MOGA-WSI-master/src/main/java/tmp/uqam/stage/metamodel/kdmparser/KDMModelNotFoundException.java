package tmp.uqam.stage.metamodel.kdmparser;

public class KDMModelNotFoundException extends RuntimeException {

    public KDMModelNotFoundException(String modelName) {
        super("Model " + modelName + " was not found in provided KDM File");
    }
}
