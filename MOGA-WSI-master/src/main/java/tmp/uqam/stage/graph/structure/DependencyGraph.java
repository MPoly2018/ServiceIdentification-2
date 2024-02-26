package tmp.uqam.stage.graph.structure;

import com.google.gson.JsonArray;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import tmp.uqam.stage.graph.visualization.SerializedGraph;
import tmp.uqam.stage.slicing.ClassSlicing;
import tmp.uqam.stage.slicing.ClassVertex;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to represent the graph of dependencies between classes
 * Graph is weighted and undirectetd
 */
public class DependencyGraph {

    protected Graph<ClassVertex, Edge> graph;

    public DependencyGraph() {
        graph = new DefaultUndirectedWeightedGraph<>(Edge.class);
    }

    /**
     * Add an adge to the graph, if the edge already exists or it's the opposite of it, it is added to the
     * weight of the edge
     */
    public void addEdge(ClassVertex source, ClassVertex target, double weight) {
        Edge e = graph.addEdge(source, target);
        if (e == null) {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Merged Edge : " + source + " -- " + target);
            e = graph.getEdge(source, target);
            double oldWeight = graph.getEdgeWeight(e);
            graph.setEdgeWeight(e, weight + oldWeight);
        } else {
            graph.setEdgeWeight(e, weight);
        }
    }

    public void addVertex(ClassVertex classVertex) {
        graph.addVertex(classVertex);
    }

    /**
     * Get the maximum spanning tree of the graph as a spanning tree, as the algorithms work only
     * for minimum spanning tree we have to invert the weight before and after
     */
    public SpanningTree<Edge> getMaximumSpanningTree() {
        invertWeights();
        SpanningTreeAlgorithm<Edge> spanningTreeAlgorithm = new PrimMinimumSpanningTree<>(graph);
        SpanningTree<Edge> spanningTree = spanningTreeAlgorithm.getSpanningTree();
        invertWeights();
        return spanningTree;
    }

    /**
     * Get the Maximum spanning tree as a graph to print or do calculations on
     */
    public SpanningTreeGraph getMaximumSpanningTreeAsGraph() {
        SpanningTreeGraph g = new SpanningTreeGraph();
        for (ClassVertex v : graph.vertexSet()) {
            g.addVertex(v);
        }
        for (Edge e : getMaximumSpanningTree().getEdges()) {
            g.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e), graph.getEdgeWeight(e));
        }
        return g;
    }

    /**
     * Utility method to invert the weights on the graph in order to get a maximum spanning tree with a minimum
     * spanning tree algorithm
     */
    private void invertWeights() {
        for (Edge e : graph.edgeSet()) {
            graph.setEdgeWeight(e, -(graph.getEdgeWeight(e)));
        }
    }

    /**
     * Remove classes that are not linked with other classes with at least the threshold number of links
     *
     * @param threshold the number of links under which the class is removed
     */
    public void removeOrphans(int threshold) {
        for (Graph<ClassVertex, Edge> g : connectedComponents()) {
            if (g.edgeSet().size() < threshold) {
                graph.removeAllVertices(g.vertexSet());
            }
        }
        // Need to reset ids to be consecutive
        int id = 0;
        for (ClassVertex vertex : graph.vertexSet()) {
            vertex.setId(id++);
        }
    }

    /**
     * Returns the connected components as subgraphs
     */
    protected Collection<Graph<ClassVertex, Edge>> connectedComponents() {
        return new BiconnectivityInspector<>(graph).getConnectedComponents();
    }

    /**
     * Obtain the weight inside of a set of vertices
     */
    public double getInterWeight(ClassSlicing vertices) {
        double weight = 0;
        Iterator<ClassVertex> iter = vertices.iterator();
        while (iter.hasNext()) {
            ClassVertex v1 = iter.next();
            for (ClassVertex v2 : vertices) {
                if (!v1.equals(v2)) {
                    Edge e = graph.getEdge(v1, v2);
                    if (e != null) {
                        weight += graph.getEdgeWeight(graph.getEdge(v1, v2));
                    }
                }
            }
            iter.remove();
        }
        return weight;
    }

    /**
     * Obtain the sum of the weight of edges connecting a set of vertices to the outside
     */
    public double getIntraWeight(ClassSlicing classes) {
        double weight = 0;
        for (ClassVertex cv : classes) {
            for (Edge e : graph.outgoingEdgesOf(cv)) {
                if (!classes.contains(graph.getEdgeTarget(e))) {
                    weight += graph.getEdgeWeight(e);
                }
            }
        }
        return weight;
    }

    public double getTotalWeight() {
        return graph.edgeSet().stream().mapToDouble(edge -> graph.getEdgeWeight(edge)).sum();
    }

    public int getTotalMethods() {
        return graph.vertexSet().stream().mapToInt(ClassVertex::getNbMethods).sum();
    }

    /**
     * returns the vertex with the id provided
     * If the id is invalid the program crashes
     */
    public ClassVertex getClassVertex(int id) {
        for (ClassVertex vertex : graph.vertexSet()) {
            if (vertex.getId() == id) {
                return vertex;
            }
        }
        throw new ClassVertexNotFoundException(id);
    }

    public ClassVertex source(Edge e) {
        return graph.getEdgeSource(e);
    }

    public ClassVertex target(Edge e) {
        return graph.getEdgeTarget(e);
    }

    /**
     * Serialize the graph for json representation
     *
     * @return a serialized graph, aka a json array for vertices and a json array for edges
     */
    public SerializedGraph serialize() {
        Set<Edge> edgeSet = graph.edgeSet();
        Set<ClassVertex> vertexSet = graph.vertexSet();
        JsonArray vertexJson = new JsonArray();
        JsonArray edgeJson = new JsonArray();
        for (ClassVertex cv : vertexSet) {
            vertexJson.add(cv.toJson());
        }
        for (Edge e : edgeSet) {
            edgeJson.add(e.toJson(source(e), target(e)));
        }
        return new SerializedGraph(vertexJson, edgeJson);
    }

    @Override
    public String toString() {
        return "DependencyGraph{" + graph + '}';
    }

    //////// TEST ////////////

    public void addEdge(ClassVertex source, ClassVertex target, Edge e, double weight) {
        graph.addEdge(source, target, e);
        graph.setEdgeWeight(e, weight);
    }

    public Graph<ClassVertex, Edge> getUnderlyingGraph() {
        return graph;
    }
}
