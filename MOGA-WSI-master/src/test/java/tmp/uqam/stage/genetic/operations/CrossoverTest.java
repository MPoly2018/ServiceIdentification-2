package tmp.uqam.stage.genetic.operations;

import org.junit.Before;
import org.junit.Test;
import tmp.uqam.stage.genetic.setup.GeneticTestConfigSetup;
import tmp.uqam.stage.genetic.subjects.Individual;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class CrossoverTest extends GeneticTestConfigSetup {

    private int phenoSize;
    private int middle;
    private Crossover crossover;

    @Before
    public void setUp() throws Exception {
        phenoSize = 10;
        middle = phenoSize / 2;
        testConfig.setPhenotypeSize(phenoSize);
        crossover = new Crossover();
    }

    @Test
    public void MiddleCrossoverTest() {
        crossover = new Crossover();
        crossover.injectRandom(new DeterministRandom(middle));
        Individual i1 = new Individual(new Random());
        Individual i2 = new Individual(new Random());
        Individual iR = crossover.onePointCrossover(i1, i2);
        for (int i = 0; i < middle; i++) {
            assertThat(iR.extractPhenotype()[i], is(i1.extractPhenotype()[i]));
        }
        for (int i = middle; i < phenoSize; i++) {
            assertThat(iR.extractPhenotype()[i], is(i2.extractPhenotype()[i]));
        }
    }

    @Test
    public void NoCrossoverTest() {
        crossover.injectRandom(new DeterministRandom(0));
        Individual i1 = new Individual(new Random());
        Individual i2 = new Individual(new Random());
        Individual iR = crossover.onePointCrossover(i1, i2);
        assertThat(iR.extractPhenotype(), is(i2.extractPhenotype()));
        assertThat(iR.extractPhenotype(), not(i1.extractPhenotype()));
    }

    @Test
    public void FullCrossoverTest() {
        crossover = new Crossover();
        crossover.injectRandom(new DeterministRandom(phenoSize));
        Individual i1 = new Individual(new Random());
        Individual i2 = new Individual(new Random());
        Individual iR = crossover.onePointCrossover(i1, i2);
        assertThat(iR.extractPhenotype(), is(i1.extractPhenotype()));
        assertThat(iR.extractPhenotype(), is(not(i2.extractPhenotype())));
    }
}
