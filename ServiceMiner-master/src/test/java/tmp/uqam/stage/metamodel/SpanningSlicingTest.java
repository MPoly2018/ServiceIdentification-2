package tmp.uqam.stage.slicing;

import org.junit.Before;
import org.junit.Test;
import tmp.uqam.stage.graph.structure.GraphTestConfigSetup;
import tmp.uqam.stage.graph.structure.SpanningTreeGraph;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpanningSlicingTest extends GraphTestConfigSetup {

    private SpanningTreeGraph sg;
    private List<WSSlicing> slicing;

    @Before
    public void setUp() throws Exception {
        sg = g.getMaximumSpanningTreeAsGraph();
        slicing = sg.getSliceProposals();
    }

    @Test
    public void testSliceSize() throws Exception {
        assertThat(slicing.size(), is(3));
        for (WSSlicing ws : slicing) {
            assertThat(ws.toString(), allOf(
                    containsString("V1"),
                    containsString("V2"),
                    containsString("V3")));
        }
    }

    @Test
    public void verifySlicingAllSizes() throws Exception {
        for (WSSlicing ws : slicing) {
            assertThat(ws.getWebservices().size(), anyOf(
                    equalTo(2),
                    equalTo(3),
                    equalTo(1)));
        }
    }

    @Test
    public void verifyValidSlicing() throws Exception {
        for (WSSlicing ws : slicing) {
            if (ws.getWebservices().size() == 2) {
                for (ClassSlicing cs : ws.getWebservices()) {
                    assertThat(cs.getClasses().toString(), anyOf(
                            // A webservice with V1 and V3 (highest coupling)
                            allOf(containsString("V1"), containsString("V3")),
                            // A webservice with only V2 (low coupling)
                            containsString("V2")
                    ));
                }
            }
        }
    }
}
