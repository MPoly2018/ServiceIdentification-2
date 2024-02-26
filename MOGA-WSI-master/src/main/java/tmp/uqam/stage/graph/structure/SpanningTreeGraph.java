package tmp.uqam.stage.graph.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jgrapht.Graph;
import tmp.uqam.stage.graph.visualization.SerializedGraph;
import tmp.uqam.stage.slicing.ClassSlicing;
import tmp.uqam.stage.slicing.ClassVertex;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.*;

/**
 * Subclass of the dependencygraph which can be user to get all the slicing proposals for
 * the genetic algorithm
 */
public class SpanningTreeGraph extends DependencyGraph {

    private List<WSSlicing> sliceProposals;

    /**
     * Constructor is package private because it should never be initialized outside of the
     * getMaximumSpanningTreeAsGraph method
     */
    SpanningTreeGraph() {
        super();
    }

    /**
     * Returns a list of slicing proposals based on the spanning tree graph, starting with the
     * smallest number of services to the largest
     */
    public List<WSSlicing> getSliceProposals() {
        if (sliceProposals == null) {
            sliceProposals = new ArrayList<>();
            WSSlicing firstProposal = new WSSlicing();
            for (Graph<ClassVertex, Edge> g : connectedComponents()) {
                firstProposal.add(new ClassSlicing(g.vertexSet()));
            }
            sliceProposals.add(firstProposal);
            while (!graph.edgeSet().isEmpty()) {
                Edge minEdge = getMinEdge();
                double minWeight = graph.getEdgeWeight(minEdge);
                graph.removeEdge(minEdge);
                while (graph.getEdgeWeight(getMinEdge()) == minWeight) {
                    graph.removeEdge(getMinEdge());
                }
                WSSlicing wsSlicing = new WSSlicing();
                for (Graph<ClassVertex, Edge> g : connectedComponents()) {
                    wsSlicing.add(new ClassSlicing(g.vertexSet()));
                }
                sliceProposals.add(wsSlicing);
            }
        }
        return sliceProposals;
    }

    /**
     * returns the edge of minimal weight
     */
    private Edge getMinEdge() {
        Optional<Edge> optionalEdge = graph.edgeSet().stream().min(Comparator.comparingDouble(e -> graph.getEdgeWeight(e)));
        return optionalEdge.orElseGet(Edge::new);
    }

    /**
     * Serialize the graph in a hierarchic way based on the slicing by different maximum weights
     */
    public SerializedGraph serializeHierarchic() {
        List<WSSlicing> proposals = new ArrayList<>();
        for (WSSlicing proposal : getSliceProposals()) {
            proposals.add(new WSSlicing(proposal));
        }
        JsonArray vertices = new JsonArray();
        JsonArray edges = new JsonArray();
        Set<ClassVertex> classes = graph.vertexSet();
        for (ClassVertex cv : classes) {
            vertices.add(cv.toJson(proposals.size() - 1, true));
        }

        WSSlicing original = proposals.get(proposals.size() - 1);
        Map<ClassSlicing, ClassSlicing> nodeFusions;
        Map<ClassSlicing, ClassVertex> nodeLinkMapping = new HashMap<>();
        for (int i = proposals.size() - 2; i >= 0; i--) {
            original.removeAll(proposals.get(i));
            nodeFusions = getNodeFusions(original, proposals.get(i));
            for (Map.Entry<ClassSlicing, ClassSlicing> classSlicing : nodeFusions.entrySet()) {
                ClassSlicing key = classSlicing.getKey();
                ClassSlicing value = classSlicing.getValue();
                ClassVertex source;
                ClassVertex target;
                if (key.size() == 1) {
                    source = key.getFirst();
                } else {
                    source = nodeLinkMapping.get(key);
                }
                if (nodeLinkMapping.get(value) == null) {
                    ClassVertex newNode = new ClassVertex(proposals.get(i).getNbWS() + " WS");
                    nodeLinkMapping.put(value, newNode);
                    vertices.add(newNode.toJson(i, false));
                }
                target = nodeLinkMapping.get(value);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("from", source.getId());
                jsonObject.addProperty("to", target.getId());
                edges.add(jsonObject);
            }
            original = proposals.get(i);
        }
        return new SerializedGraph(vertices, edges);
    }

    /**
     * private method to retrieve all the fusion from a slicing to another one with less services
     *
     * @param originalProposal the original slicing
     * @param newProposal      the one with at least one less service (so at least one fusion)
     * @return a map which links the classslicing from the original with the new ones when they are fused
     */
    private Map<ClassSlicing, ClassSlicing> getNodeFusions(WSSlicing originalProposal, WSSlicing newProposal) {
        Map<ClassSlicing, ClassSlicing> nodeFusions = new HashMap<>();
        for (ClassSlicing originalSlicing : originalProposal) {
            for (ClassSlicing newSlicing : newProposal) {
                if (newSlicing.containsAll(originalSlicing)) {
                    nodeFusions.put(originalSlicing, newSlicing);
                    break;
                }
            }
        }
        return nodeFusions;
    }

}
