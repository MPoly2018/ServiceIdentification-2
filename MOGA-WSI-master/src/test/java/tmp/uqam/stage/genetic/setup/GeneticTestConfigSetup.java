package tmp.uqam.stage.genetic.setup;

import org.junit.Before;
import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.graph.structure.DependencyGraph;
import tmp.uqam.stage.slicing.ClassSlicing;
import tmp.uqam.stage.slicing.ClassVertex;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.*;

public abstract class GeneticTestConfigSetup {

    protected GeneticConfiguration testConfig;

    @Before
    public void setUpBoss() {
        testConfig = GeneticConfiguration.getConfig();
        testConfig.setEliteNumber(5);
        testConfig.setPhenotypeSize(54);
        testConfig.setMutationRate(0.05);
        testConfig.setWeights(1, 0, 0, 0, 0);
        testConfig.setCrossoverRate(0.8);
        testConfig.setPopSize(20);
        testConfig.setTournamentSize(5);
        initGraphSlicing();
    }

    private void initGraphSlicing() {
        DependencyGraph g = new DependencyGraph();
        ClassVertex.resetGID();
        List<ClassVertex> vertices = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            vertices.add(new ClassVertex("V" + i));
        }
        Random r = new Random();
        for (ClassVertex v : vertices) {
            v.setNbMethods(r.nextInt(10));
            g.addVertex(v);
        }
        Iterator<ClassVertex> iter = vertices.iterator();
        while (iter.hasNext()) {
            ClassVertex v1 = iter.next();
            for (ClassVertex v2 : vertices) {
                if (!v1.equals(v2) && r.nextBoolean()) {
                    g.addEdge(v1, v2, r.nextInt(100) / 10.0);
                }
            }
            iter.remove();
        }
        testConfig.setGraph(g);
        testConfig.setServiceNumber(3);
        testConfig.setClassNumber(18);
    }

    public static WSSlicing initTestSlicing() {
        ClassVertex.resetGID();
        int ws = 4;
        WSSlicing wsSlicing = new WSSlicing();
        for (int i = 0; i < ws; i++) {
            Set<ClassVertex> classVertices = new HashSet<>();
            for (int j = 0; j < 2; j++) {
                classVertices.add(new ClassVertex("C" + i + j));
            }
            wsSlicing.add(new ClassSlicing(classVertices));
        }
        return wsSlicing;
    }
}
