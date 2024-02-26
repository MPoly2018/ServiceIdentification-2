package tmp.uqam.stage.metamodel.kdmparser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import tmp.uqam.stage.metamodel.LinkType;
import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.metamodel.MetaModelParser;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class KDMParserTest {

    private MetaModelParser parser;


    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void systemExitWhenInvalid() {
        exit.expectSystemExitWithStatus(1);
        parser = new KDMMetaModelParser("INVALIDFILE", "modleo", true);
    }

    @Test(expected = KDMModelNotFoundException.class)
    public void exceptionWhenInvalidModel() {
        parser = new KDMMetaModelParser("test/miniTest_kdm.xmi", "INVALIDMODEL", true);
    }

    @Test
    public void parsingTest() {
        parser = new KDMMetaModelParser("test/miniTest_kdm.xmi", "miniTest", true);
        MetaModel mm = parser.extractMetaModel();
        assertThat(mm.getClasses().size(), equalTo(6));
        assertThat(mm.getLinkWeights().size(), equalTo(7));
        Collection<Map<LinkType, Integer>> links = ((KDMMetaModelParser) parser).getLinkMap().getMap().values();
        boolean multipleTypeLink = false;
        for (Map<LinkType, Integer> link : links) {
            if (link.values().size() > 1) {
                multipleTypeLink = true;
            }
        }
        assertThat(multipleTypeLink, is(true));
    }
}
