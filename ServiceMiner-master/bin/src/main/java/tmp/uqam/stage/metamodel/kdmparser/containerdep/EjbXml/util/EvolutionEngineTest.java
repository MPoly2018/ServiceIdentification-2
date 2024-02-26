package tmp.uqam.stage.genetic;

import org.junit.Ignore;
import org.junit.Test;
import tmp.uqam.stage.genetic.setup.GeneticTestConfigSetup;
import tmp.uqam.stage.genetic.subjects.Individual;
import tmp.uqam.stage.slicing.WSSlicing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class EvolutionEngineTest extends GeneticTestConfigSetup {

    @Test
    public void keepSamePopSizeTest() throws Exception {
        EvolutionEngine engine = new EvolutionEngine();
        int popSizeInit = engine.getPopulation().getPopSize();
        assertThat(popSizeInit, equalTo(testConfig.getPopSize()));
        for (int i = 0; i < 10; i++) {
            engine.iterate();
        }
        assertThat(popSizeInit, equalTo(engine.getPopulation().getPopSize()));
    }

    @Test
    public void keepSamePopSizeTestOddSize() throws Exception {
        testConfig.setPopSize(21);
        keepSamePopSizeTest();
    }


    @Test
    @Ignore
    public void convergenceTest() throws Exception {
        EvolutionEngine engine = new EvolutionEngine();
        engine.iterate();
        double meanFitness = engine.getPopulation().extractPopulation().stream().mapToDouble(Individual::getFitness).average().getAsDouble();
        for (int i = 0; i < 25; i++) {
            engine.iterate();
        }
        double newMeanFitness = engine.getPopulation().extractPopulation().stream().mapToDouble(Individual::getFitness).average().getAsDouble();
        assertThat(meanFitness, lessThanOrEqualTo(newMeanFitness));
    }

    @Test
    public void initConfig() throws Exception {
        WSSlicing slicing = GeneticTestConfigSetup.initTestSlicing();
        slicing.initConfig();
        assertThat(testConfig.getClassNumber(), equalTo(slicing.getNbClasses()));
        assertThat(testConfig.getServiceNumber(), equalTo(slicing.getNbWS()));
        assertThat(testConfig.getPhenotypeSize(), equalTo(slicing.getNbWS() * slicing.getNbClasses()));
    }
}
