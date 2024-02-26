package tmp.uqam.stage.slicing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tmp.uqam.stage.graph.visualization.SerializedGraph;

import java.util.*;

/**
 * Basically just a class to simplify a set of classvertex
 */
public class ClassSlicing implements Iterable<ClassVertex> {

    private Set<ClassVertex> classes;

    public ClassSlicing(Set<ClassVertex> classes) {
        this.classes = classes;
    }

    /**
     * Serialize the slicing for a 'clustered' representation of webservices
     */
    public SerializedGraph serialize() {
        JsonArray edgesJson = new JsonArray();
        JsonArray verticesJson = new JsonArray();
        if (classes.size() > 0) {
            int originId = new ArrayList<>(classes).get(0).getId();
            for (ClassVertex cv : classes) {
                verticesJson.add(cv.toJson());
                if (cv.getId() != originId) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("from", originId);
                    jsonObject.addProperty("to", cv.getId());
                    edgesJson.add(jsonObject);
                }
            }
        }
        return new SerializedGraph(verticesJson, edgesJson);
    }

    public int size() {
        return classes.size();
    }

    public void addAll(ClassSlicing classSlicing) {
        this.classes.addAll(classSlicing.classes);
    }

    public boolean contains(ClassVertex edgeTarget) {
        return this.classes.contains(edgeTarget);
    }

    public boolean containsAll(ClassSlicing vertices) {
        return this.classes.containsAll(vertices.getClasses());
    }

    public ClassVertex getFirst() {
        List<ClassVertex> list = new ArrayList<ClassVertex>(classes);
        return list.get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSlicing that = (ClassSlicing) o;
        return Objects.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classes);
    }

    @Override
    public Iterator<ClassVertex> iterator() {
        return classes.iterator();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        classes.forEach(v -> sj.add(v.toString()));
        return sj.toString();
    }


    /////// TEST ////////

    public Set<ClassVertex> getClasses() {
        return classes;
    }
}
