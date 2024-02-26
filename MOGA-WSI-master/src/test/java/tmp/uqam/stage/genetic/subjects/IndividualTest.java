package tmp.uqam.stage.genetic.subjects;

import org.junit.Test;
import tmp.uqam.stage.genetic.setup.GeneticTestConfigSetup;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class IndividualTest extends GeneticTestConfigSetup {

    @Test
    public void comparisonTest() {
        Individual iGood = new Individual(new Random());
        Individual iGoodToo = new Individual(new Random());
        Individual iBad = new Individual(new Random());
        iGood.setFitness(100.0);
        iGoodToo.setFitness(100.0);
        iBad.setFitness(10.0);
        assertThat(iGood.compareTo(iBad), greaterThan(0));
        assertThat(iBad.compareTo(iGood), lessThan(0));
        assertThat(iGood.compareTo(iGoodToo), is(0));
    }

    @Test
    public void initializeTest() {
        Individual iInitialized = new Individual(new Random());
        Individual iUnInitialized = new Individual();
        assertThat(iInitialized.extractPhenotype().length, is(iUnInitialized.extractPhenotype().length));
        assertThat(iInitialized.extractPhenotype().length, is(testConfig.getPhenotypeSize()));
        assertThat(iInitialized.getFitness(), greaterThanOrEqualTo(iUnInitialized.getFitness()));
    }

    @Test
    public void initializeSlicingTest() {
        WSSlicing wsSlicing = GeneticTestConfigSetup.initTestSlicing();
        wsSlicing.initConfig();
        Individual individual = new Individual(wsSlicing);
        assertThat(individual.extractPhenotype(), equalTo(new boolean[]{
                true, true, false, false, false, false, false, false,
                false, false, true, true, false, false, false, false,
                false, false, false, false, true, true, false, false,
                false, false, false, false, false, false, true, true,}));
        assertThat(individual.getPhenotypeAsWSSlicing().getNbWS(), is(4));
        assertThat(individual.getPhenotypeAsWSSlicing().getNbClasses(), is(8));
    }
}
