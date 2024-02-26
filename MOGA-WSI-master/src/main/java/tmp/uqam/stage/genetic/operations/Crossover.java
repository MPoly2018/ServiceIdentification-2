package tmp.uqam.stage.genetic.operations;

import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.subjects.Individual;

import java.util.Random;

/**
 * Crossover utilities
 */
public class Crossover {

    private Random random;

    public Crossover() {
        random = new Random();
    }

    /**
     * Takes two parents and creates an individual starting with i1 and finishing with i2 based on a random
     * point in the midddle.
     *
     * @param i1 parent 1
     * @param i2 parent 2
     * @return the newborn individual
     */
    public Individual onePointCrossover(Individual i1, Individual i2) {
        GeneticConfiguration config = GeneticConfiguration.getConfig();
        int crossPoint = random.nextInt(config.getPhenotypeSize() - 1);
        Individual newBorn = new Individual();
        for (int i = 0; i < crossPoint; i++) {
            newBorn.set(i, i1.get(i));
        }
        for (int i = crossPoint; i < config.getPhenotypeSize(); i++) {
            newBorn.set(i, i2.get(i));
        }
        return newBorn;
    }

    //////////// TEST ////////////
    public void injectRandom(Random random) {
        this.random = random;
    }
}
