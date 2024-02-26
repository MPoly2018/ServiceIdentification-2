package tmp.uqam.stage.graph.structure;

import com.google.gson.JsonObject;
import org.jgrapht.graph.DefaultWeightedEdge;
import tmp.uqam.stage.slicing.ClassVertex;

/**
 * Basic edge implementation
 */
public class Edge extends DefaultWeightedEdge {

    @Override
    public String toString() {
        return String.format("%.2f", getWeight());
    }

    /**
     * Serialize the edge in json format for graphical representation
     */
    public JsonObject toJson(ClassVertex source, ClassVertex target) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from", source.getId());
        jsonObject.addProperty("to", target.getId());
        jsonObject.addProperty("label", toString());
        jsonObject.addProperty("value", (new Double(getWeight() * 10)).intValue());
        return jsonObject;
    }
}
