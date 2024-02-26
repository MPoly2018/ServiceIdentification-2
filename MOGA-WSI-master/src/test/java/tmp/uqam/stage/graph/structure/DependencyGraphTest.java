package tmp.uqam.stage.graph.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.junit.Test;
import tmp.uqam.stage.graph.visualization.SerializedGraph;
import tmp.uqam.stage.slicing.ClassType;
import tmp.uqam.stage.slicing.ClassVertex;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DependencyGraphTest extends GraphTestConfigSetup {

    @Test
    public void checkSize() throws Exception {
        assertThat(g.getUnderlyingGraph().edgeSet().size(), equalTo(3));
        assertThat(g.getUnderlyingGraph().vertexSet().size(), equalTo(3));
    }

    @Test
    public void checkContaining() throws Exception {
        assertThat(g.getUnderlyingGraph().edgeSet(), containsInAnyOrder(e1, e2, e3));
        assertThat(g.getUnderlyingGraph().vertexSet(), containsInAnyOrder(v1, v2, v3));
    }

    @Test
    public void checkConnections() throws Exception {
        assertThat(g.getUnderlyingGraph().containsEdge(v1, v2), is((true)));
        assertThat(g.getUnderlyingGraph().containsEdge(v2, v3), is((true)));
        assertThat(g.getUnderlyingGraph().containsEdge(v3, v1), is((true)));
    }

    @Test
    public void spanningSize() throws Exception {
        SpanningTreeAlgorithm.SpanningTree<Edge> st = g.getMaximumSpanningTree();
        assertThat(st.getEdges().size(), is(2));
        SpanningTreeGraph sg = g.getMaximumSpanningTreeAsGraph();
        assertThat(sg.getUnderlyingGraph().edgeSet().size(), is(2));
    }

    @Test
    public void testMergeDuplicateEdges() {
        assertThat(g.getUnderlyingGraph().getEdgeWeight(e1), closeTo(1.0, 0.01));
        g.addEdge(v2, v1, 1.0);
        assertThat(g.getUnderlyingGraph().getEdgeWeight(e1), closeTo(2.0, 0.01));
        g.addEdge(v1, v2, 1.0);
        assertThat(g.getUnderlyingGraph().getEdgeWeight(e1), closeTo(3.0, 0.01));
    }

    @Test
    public void testRemoveOrphans() {
        g.addVertex(new ClassVertex("solo"));
        ClassVertex brother = new ClassVertex("brother");
        ClassVertex sister = new ClassVertex("sister");
        g.addVertex(brother);
        g.addVertex(sister);
        g.addEdge(brother, sister, 1.0);
        assertThat(g.getUnderlyingGraph().vertexSet().size(), is(6));
        g.removeOrphans(1);
        assertThat(g.getUnderlyingGraph().vertexSet().size(), is(5));
        g.removeOrphans(2);
        assertThat(g.getUnderlyingGraph().vertexSet().size(), is(3));
        assertThat(g.getUnderlyingGraph().vertexSet(), not(containsInAnyOrder(sister, brother)));
    }

    @Test
    public void serializationGraphTest() {
        SerializedGraph sg = g.serialize();
        JsonArray vertices = sg.getVertices();
        JsonArray edges = sg.getEdges();
        assertThat(vertices.size(), is(3));
        assertThat(edges.size(), is(3));
        JsonObject v1 = createJsonVertex(0, "V1", ClassType.INTERFACE.getShape());
        JsonObject v2 = createJsonVertex(1, "V2", ClassType.CLASS.getShape());
        JsonObject v3 = createJsonVertex(2, "V3", ClassType.ENUM.getShape());
        JsonObject e1 = createJsonEdge(0, 1, 1.0);
        JsonObject e2 = createJsonEdge(1, 2, 2.0);
        JsonObject e3 = createJsonEdge(2, 0, 3.0);
        assertThat(vertices, containsInAnyOrder(v1, v2, v3));
        assertThat(edges, containsInAnyOrder(e1, e2, e3));
    }

    @Test
    public void serializationSpanningGraphTest() {
        SpanningTreeGraph stg = g.getMaximumSpanningTreeAsGraph();
        SerializedGraph sg = stg.serialize();
        JsonArray vertices = sg.getVertices();
        JsonArray edges = sg.getEdges();
        assertThat(vertices.size(), is(3));
        assertThat(edges.size(), is(2));
        JsonObject v1 = createJsonVertex(0, "V1", ClassType.INTERFACE.getShape());
        JsonObject v2 = createJsonVertex(1, "V2", ClassType.CLASS.getShape());
        JsonObject v3 = createJsonVertex(2, "V3", ClassType.ENUM.getShape());
        JsonObject e1 = createJsonEdge(1, 2, 2.0);
        JsonObject e2 = createJsonEdge(2, 0, 3.0);
        assertThat(vertices, containsInAnyOrder(v1, v2, v3));
        assertThat(edges, containsInAnyOrder(e1, e2));
    }


    @Test
    public void hierarchicSerializationSpanningGraphTest() {
        SpanningTreeGraph stg = g.getMaximumSpanningTreeAsGraph();
        SerializedGraph sg = stg.serializeHierarchic();
        JsonArray vertices = sg.getVertices();
        JsonArray edges = sg.getEdges();
        System.out.println(sg.getVertices());
        System.out.println(sg.getEdges());
        assertThat(vertices.size(), is(5));
        assertThat(edges.size(), is(4));
        int i = 0;
        for (; i < 3; i++) {
            assertThat(vertices.get(i).getAsJsonObject().get("level").getAsInt(), is(2));
        }
        assertThat(vertices.get(i++).getAsJsonObject().get("level").getAsInt(), is(1));
        assertThat(vertices.get(i).getAsJsonObject().get("level").getAsInt(), is(0));
        JsonObject e1 = createBaseJsonEdge(0, 3);
        JsonObject e2 = createBaseJsonEdge(2, 3);    //       4
        JsonObject e3 = createBaseJsonEdge(1, 4);    //               3
        JsonObject e4 = createBaseJsonEdge(3, 4);    //   1       0        2
        assertThat(edges, containsInAnyOrder(e1, e2, e3, e4));
    }

    ///////////////////////////////


    private JsonObject createJsonVertex(int id, String label, String type) {
        JsonObject v = new JsonObject();
        v.addProperty("id", id);
        v.addProperty("label", label);
        v.addProperty("shape", type);
        return v;
    }

    private JsonObject createJsonEdge(int from, int to, Double weight) {
        JsonObject e = new JsonObject();
        e.addProperty("from", from);
        e.addProperty("to", to);
        e.addProperty("label", String.format("%.2f", weight));
        e.addProperty("value", weight * 10);
        return e;
    }

    private JsonObject createBaseJsonEdge(int from, int to) {
        JsonObject e = new JsonObject();
        e.addProperty("from", from);
        e.addProperty("to", to);
        return e;
    }
}
