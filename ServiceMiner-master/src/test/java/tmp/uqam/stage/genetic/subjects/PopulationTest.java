package tmp.uqam.stage.genetic.subjects;

import org.junit.Test;
import tmp.uqam.stage.genetic.criteria.WeightedObjectiveFitness;
import tmp.uqam.stage.genetic.setup.GeneticTestConfigSetup;

import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PopulationTest extends GeneticTestConfigSetup {

    @Test
    public void sizeTest() {
        Population p = new Population(new Random());
        assertThat(p.getPopSize(), is(testConfig.getPopSize()));
        if (p.getPopSize() > 0) {
            assertThat(p.get(0), is(instanceOf(Individual.class)));
            assertThat(p.get(0).extractPhenotype().length, is(testConfig.getPhenotypeSize()));
        }
    }

    @Test
    public void applyTest() {
        Population p = new Population(new Random());
        p.apply(new WeightedObjectiveFitness());
        for (Individual i : p.extractPopulation()) {
            assertThat(i.getFitness(), greaterThan(0.0));
        }
    }

    @Test
    public void saveElitesTest() {
        int popSize = 3;
        int eliteNumber = 2;
        Population p = new Population(popSize);
        testConfig.setEliteNumber(eliteNumber);
        for (int i = 1; i < 4; i++) {
            Individual individual = new Individual(new Random());
            individual.setFitness((double) i);
            p.add(individual);
        }
        assertThat(p.getPopSize(), is(popSize));
        Population p2 = p.saveElites();
        assertThat(p.getPopSize(), is(popSize));
        assertThat(p2.getPopSize(), is(eliteNumber));
        assertThat(p2.fittest().getFitness(), is(3.0));
        assertTrue(p2.extractPopulation().stream().anyMatch(individual -> individual.getFitness() == 3.0));
        assertTrue(p2.extractPopulation().stream().anyMatch(individual -> individual.getFitness() == 2.0));
        assertTrue(p2.extractPopulation().stream().noneMatch(individual -> individual.getFitness() == 1.0));
        assertThat(p.fittest().getFitness(), is(3.0));
    }

    @Test
    public void resizeShrinkTest() {
        int popSize = 10;
        testConfig.setPopSize(popSize);
        Population p = new Population(popSize);
        for (int i = 0; i < popSize; i++) {
            Individual individual = new Individual(new Random());
            individual.setFitness((double) i);
            p.add(individual);
        }
        testConfig.setPopSize(8);
        p.resize();
        assertThat(testConfig.getPopSize(), is(8));
        assertThat(p.getPopSize(), is(8));
        for (Individual i : p.extractPopulation()) {
            assertThat(i.getFitness(), greaterThanOrEqualTo(2.0));
        }
    }

    @Test
    public void resizeGrowTest() {
        int popSize = 10;
        testConfig.setPopSize(popSize);
        Population p = new Population(popSize);
        for (int i = 0; i < popSize; i++) {
            Individual individual = new Individual(new Random());
            individual.setFitness((double) i);
            p.add(individual);
        }
        testConfig.setPopSize(15);
        p.resize();
        assertThat(testConfig.getPopSize(), is(15));
        assertThat(p.getPopSize(), is(15));
        for (Individual i : p.extractPopulation()) {
            assertThat(i.getFitness(), notNullValue());
        }
    }
}
