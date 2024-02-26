package tmp.uqam.stage.slicing;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * The class to represent a class (or interface/enum) which is also a vertex in the graph
 */
public class ClassVertex implements Serializable {

    private static int GLOBAL_ID = 0;
    private int id;
    private String name;
    private int nbMethods;
    private ClassType classType;

    /**
     * Manual id management
     */
    public ClassVertex(int id, String name) {
        this.id = id;
        this.name = name;
        this.nbMethods = 0;
    }

    /**
     * Automatic ID management
     */
    public ClassVertex(String name) {
        this.name = name;
        this.id = GLOBAL_ID++;
    }

    /**
     * Convert this to a json object for the basic graphical representation
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("label", name);
        jsonObject.addProperty("shape", classType.getShape());
        return jsonObject;
    }

    /**
     * Convert this to a json object for the hierarchical graphical representation
     *
     * @param level the level on the hierarchic tree
     * @param shape if we apply a shape or not (true for leaves and false for nodes)
     */
    public JsonObject toJson(int level, boolean shape) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("label", name);
        jsonObject.addProperty("level", level);
        if (shape) {
            jsonObject.addProperty("shape", classType.getShape());
        } else {
            jsonObject.addProperty("shape", "text");
        }
        return jsonObject;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getNbMethods() {
        return nbMethods;
    }

    public void setNbMethods(int nbMethods) {
        this.nbMethods = nbMethods;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    @Override
    public String toString() {
        return name;
    }


    ///////TEST////////

    public static void resetGID() {
        GLOBAL_ID = 0;
    }

}
