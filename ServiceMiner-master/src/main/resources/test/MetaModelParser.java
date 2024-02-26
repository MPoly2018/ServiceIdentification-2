package tmp.uqam.stage.metamodel;

import java.util.Map;

import tmp.uqam.stage.metamodel.kdmparser.Metrics;

/**
 * Generic file parser that returns a Metamodel to be converted to a graph
 */
public abstract class MetaModelParser extends ModelReader {
	
	public Map<String,Metrics> Class_Metrics;

    public MetaModelParser(String fileName, boolean resource) {
        super(fileName, resource);
    }

    public abstract MetaModel extractMetaModel();
}
