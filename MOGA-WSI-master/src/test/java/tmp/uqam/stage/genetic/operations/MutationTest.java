package tmp.uqam.stage.genetic.operations;

import org.junit.Test;
import tmp.uqam.stage.genetic.setup.GeneticTestConfigSetup;
import tmp.uqam.stage.genetic.subjects.Individual;

import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class MutationTest extends GeneticTestConfigSetup {

    @Test
    public void fullMutate() {
        testConfig.setMutationRate(100);
        Individual individual = new Individual(new Random());
        boolean[] original = individual.extractPhenotype();
        new Mutation().mutate(individual);
        for (int i = 0; i < testConfig.getPhenotypeSize(); i++) {
            assertThat(individual.get(i), not(original[i]));
        }
    }

    @Test
    public void noMutate() {
        testConfig.setMutationRate(0);
        Individual individual = new Individual(new Random());
        boolean[] original = individual.extractPhenotype();
        new Mutation().mutate(individual);
        assertThat(individual.extractPhenotype(), is(original));
    }
}
