package tmp.uqam.stage.metamodel;

import org.junit.Before;
import org.junit.Test;
import tmp.uqam.stage.slicing.ClassVertex;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MetaModelTest {

    private LinkMap lm;
    private ClassVertex c1, c2, c3;
    private Link l1, l2;

    @Before
    public void setUp() throws Exception {
        lm = new LinkMap();
        c1 = new ClassVertex("C1");
        c2 = new ClassVertex("C2");
        c3 = new ClassVertex("C2");
        l1 = new Link(c1, c2);
        l2 = new Link(c2, c3);
    }

    @Test
    public void testLinkOrder() {
        assertThat(l1.getFrom(), is(c1));
        assertThat(l1.getTo(), is(c2));
    }

    @Test
    public void checkLinkMapSize() {
        lm.put(l1, LinkType.CONSTRUCTOR_INVOKE);
        assertThat(lm.getMap().size(), is(1));
        lm.put(l1, LinkType.ONEWAY);
        assertThat(lm.getMap().size(), is(1));
        lm.put(l2, LinkType.ONEWAY);
        assertThat(lm.getMap().size(), is(2));
    }

    @Test
    public void checkLinkMapSet() {
        lm.put(l1, LinkType.METHOD_INVOKE);
        lm.put(l1, LinkType.ONEWAY);
        assertThat(lm.getMap().containsKey(l1), is(true));
        assertThat(lm.getMap().get(l1).keySet(), containsInAnyOrder(LinkType.METHOD_INVOKE, LinkType.ONEWAY));
        assertThat(lm.getMap().get(l1).keySet(), not(contains(LinkType.EXTENDS)));
    }

    @Test
    public void verifyMigratedLinksGreater() {
        lm.put(l1, LinkType.IMPLEMENTS);
        lm.put(l1, LinkType.METHOD_INVOKE);
        lm.put(l2, LinkType.METHOD_INVOKE);
        Map<Link, Double> migratedLinks = lm.migrateAndComputeLinks();
        assertThat(migratedLinks.size(), is(2));
        assertThat(migratedLinks.keySet(), containsInAnyOrder(l1, l2));
        assertThat(migratedLinks.get(l1), greaterThan(migratedLinks.get(l2)));
    }

}
