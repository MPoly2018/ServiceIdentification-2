package tmp.uqam.stage.metamodel;

/**
 * Generic file parser that returns a Metamodel to be converted to a graph
 */
public abstract class MetaModelParser extends ModelReader {

    public MetaModelParser(String fileName, boolean resource) {
        super(fileName, resource);
    }

    public abstract MetaModel extractMetaModel();
}
