package tmp.uqam.stage.graph.visualization;

import com.google.gson.JsonArray;

/**
 * A graph format for a visual representation with vis, composed of two jsonarrays, one for edges and one for vertives
 */
public class SerializedGraph {

    private JsonArray vertices;
    private JsonArray edges;

    public SerializedGraph(JsonArray vertices, JsonArray edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public JsonArray getVertices() {
        return vertices;
    }

    public JsonArray getEdges() {
        return edges;
    }

}
