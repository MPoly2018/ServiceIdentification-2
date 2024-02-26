package tmp.uqam.stage.graph.structure;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Terminating exception for when a classvertex is not found on the graph given an id
 */
public class ClassVertexNotFoundException extends RuntimeException {

    public ClassVertexNotFoundException(int id) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Vertex with id " + id + " does not exists");
       // System.exit(1);
    }
}
