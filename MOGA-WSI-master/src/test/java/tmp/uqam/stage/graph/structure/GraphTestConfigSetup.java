package tmp.uqam.stage.graph.structure;

import org.junit.Before;
import tmp.uqam.stage.slicing.ClassType;
import tmp.uqam.stage.slicing.ClassVertex;

public class GraphTestConfigSetup {

    protected DependencyGraph g;
    protected ClassVertex v1, v2, v3;
    protected Edge e1, e2, e3;

    @Before
    public void setUpBoss() throws Exception {
        ClassVertex.resetGID();
        g = new DependencyGraph();
        v1 = new ClassVertex("V1");
        v2 = new ClassVertex("V2");
        v3 = new ClassVertex("V3");
        v1.setClassType(ClassType.INTERFACE);
        v2.setClassType(ClassType.CLASS);
        v3.setClassType(ClassType.ENUM);
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        e1 = new Edge();
        e2 = new Edge();
        e3 = new Edge();
        g.addEdge(v1, v2, e1, 1.0);
        g.addEdge(v2, v3, e2, 2.0);
        g.addEdge(v3, v1, e3, 3.0);
    }
}
