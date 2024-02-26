package tmp.uqam.stage.metamodel.jsonparser;

import org.junit.Before;
import org.junit.Test;
import tmp.uqam.stage.metamodel.Link;
import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.slicing.ClassVertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class MetaModelEnhancerTest {

    private ClassVertex c1;
    private ClassVertex c2;
    private ClassVertex c3;
    private Set<ClassVertex> classVertices;
    private Map<Link, Double> linkWeights;
    private MetaModelEnhancer metaModelEnhancer;
    private MetaModel metaModel;

    @Before
    public void setUp() {
        c1 = new ClassVertex("C1");
        c2 = new ClassVertex("C2");
        c3 = new ClassVertex("C3");
        classVertices = new HashSet<>();
        linkWeights = new HashMap<>();
    }

    @Test
    public void basicEnhanceTest() {
        metaModelEnhancer = new MetaModelEnhancer("test/miniTest.json", true);
        classVertices.add(c1);
        classVertices.add(c2);
        classVertices.add(c3);
        metaModel = new MetaModel(linkWeights, classVertices);
        assertThat(metaModel.getClasses().size(), is(3));
        assertThat(metaModel.getLinkWeights().size(), is(0));
        metaModelEnhancer.enhanceMetaModel(metaModel);
        assertThat(metaModel.getLinkWeights().size(), is(3));
        assertThat(metaModel.getLinkWeights().get(new Link(c3, c2)), greaterThan(metaModel.getLinkWeights().get(new Link(c1, c2))));
    }

    @Test
    public void enhanceTestWithSomeConnections() {
        metaModelEnhancer = new MetaModelEnhancer("test/miniTest.json", true);
        classVertices.add(c1);
        classVertices.add(c2);
        classVertices.add(c3);
        linkWeights.put(new Link(c1, c2), 2.0);
        linkWeights.put(new Link(c3, c2), 1.0);
        metaModel = new MetaModel(linkWeights, classVertices);
        assertThat(metaModel.getClasses().size(), is(3));
        assertThat(metaModel.getLinkWeights().size(), is(2));
        metaModelEnhancer.enhanceMetaModel(metaModel);
        assertThat(metaModel.getLinkWeights().size(), is(3));
        assertThat(metaModel.getLinkWeights().get(new Link(c3, c2)), greaterThan(metaModel.getLinkWeights().get(new Link(c1, c2))));
    }

    @Test
    public void enhanceTestMalformedFile() {
        metaModelEnhancer = new MetaModelEnhancer("test/miniTest_malformed.json", true);
        classVertices.add(c1);
        classVertices.add(c2);
        classVertices.add(c3);
        metaModel = new MetaModel(linkWeights, classVertices);
        metaModelEnhancer.enhanceMetaModel(metaModel);
    }

    @Test
    public void enhanceTestNonExistingClasses() {
        metaModelEnhancer = new MetaModelEnhancer("test/miniTest.json", true);
        classVertices.add(c1);
        classVertices.add(c2);
        metaModel = new MetaModel(linkWeights, classVertices);
        metaModelEnhancer.enhanceMetaModel(metaModel);
        assertThat(metaModel.getLinkWeights().size(), is(1));
    }
}
