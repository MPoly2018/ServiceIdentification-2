package tmp.uqam.stage.slicing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.graph.visualization.SerializedGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

/**
 * Representatino of an architecture proposals -> a set of webservices
 */
public class WSSlicing implements Iterable<ClassSlicing> {

    private List<ClassSlicing> webservices;

    public WSSlicing() {
        this.webservices = new ArrayList<>();
    }

    /**
     * Copy constructor (shallow)
     */
    public WSSlicing(WSSlicing toCopy) {
        this.webservices = new ArrayList<>(toCopy.webservices);
    }

    /**
     * Returns the architecture proposal in a binary representation of size nbClasses*nbWS
     * it's a linearized array of array where each array is a boolean array of a webservice
     * where the value of a case is true if the class of the id of the case is in the webservice
     * or false otherwise.
     */
    public boolean[] getBinaryRepresentation() {
        boolean[] tab = new boolean[getNbClasses() * getNbWS()];
        for (int i = 0; i < webservices.size(); i++) {
            for (ClassVertex cv : webservices.get(i)) {
                tab[i * getNbClasses() + cv.getId()] = true;
            }
        }
        return tab;
    }

    /**
     * Sets the correct static values for the genetic configuration
     */
    public void initConfig() {
        GeneticConfiguration config = GeneticConfiguration.getConfig();
        config.setClassNumber(getNbClasses());
        config.setServiceNumber(getNbWS());
        config.setPhenotypeSize(getNbClasses() * getNbWS());
    }

    /**
     * Serialize the Webservice slicing to create a clustered visualization of the slicing
     */
    public SerializedGraph serialize() {
        JsonArray verticesJson = new JsonArray();
        JsonArray edgesJson = new JsonArray();
        SerializedGraph serializedWS;
        for (int i = 0; i < webservices.size(); i++) {
            serializedWS = webservices.get(i).serialize();
            for (JsonElement elem : serializedWS.getVertices()) {
                JsonObject jElem = (JsonObject) elem;
                migrateId(i, jElem, "id");
                verticesJson.add(elem);
            }
            for (JsonElement elem : serializedWS.getEdges()) {
                JsonObject jElem = (JsonObject) elem;
                migrateId(i, jElem, "from");
                migrateId(i, jElem, "to");
                edgesJson.add(elem);
            }
        }
        return new SerializedGraph(verticesJson, edgesJson);
    }

    /**
     * Utility method to avoid to have the same id multiple time if a class is duplicated and crash the javascript
     *
     * @param serviceId the id of the service where the lass is located
     * @param jElem     the class
     * @param property  the property to change (from or to)
     */
    private void migrateId(int serviceId, JsonObject jElem, String property) {
        int oldId = jElem.get(property).getAsInt();
        jElem.remove(property);
        jElem.addProperty(property, serviceId * 10000 + oldId);
    }

    public void add(ClassSlicing classSlicing) {
        this.webservices.add(classSlicing);
    }

    public List<ClassSlicing> getWebservices() {
        return webservices;
    }

    public int getNbClasses() {
        return webservices.stream().mapToInt(ClassSlicing::size).sum();
    }

    public int getNbWS() {
        return webservices.size();
    }

    public void removeAll(WSSlicing other) {
        this.webservices.removeAll(other.webservices);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner("\t\t", webservices.size() + " webservices [ ", " ]\n");
        webservices.forEach(v -> sj.add(v.toString()));
        return sj.toString();
    }

    @Override
    public Iterator<ClassSlicing> iterator() {
        return webservices.iterator();
    }
}
