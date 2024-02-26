package tmp.uqam.stage.genetic.operations;

import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.subjects.Individual;

import java.util.Random;

/**
 * Mutation utilities
 */
public class Mutation {

    /**
     * Apply potentially foreach individual a mutation which will change one of his genes 0->1 or 1->0
     *
     * @param individual the individual to mutate
     */
    public void mutate(Individual individual) {
        Random random = new Random();
        for (int i = 0; i < GeneticConfiguration.getConfig().getPhenotypeSize(); i++) {
            if (random.nextInt(100) < GeneticConfiguration.getConfig().getMutationRate() * 100) {
                individual.set(i, !(individual.get(i)));
            }
        }
    }

    /**
     * A smart mutation that will swap classes from a webservice to another to avoid invalid individuals
     * ther is still a probability for an individual to mutate the old way in order to add a bit of salt
     * to our population
     * <p>
     * Probability to mutate is multiplied by the number of services M because it needs to a be a 1 to mutate
     * and the ratios of 1 is 1/M
     *
     * @param individual the individual to mutate
     */
    public void smartMutate(Individual individual) {
        Random random = new Random();
        GeneticConfiguration config = GeneticConfiguration.getConfig();
        if (random.nextInt(100) < config.getMutationRate() * config.getServiceNumber() * 100) {
            mutate(individual);
        } else {
            for (int i = 0; i < config.getPhenotypeSize(); i++) {
                if (individual.get(i) && random.nextInt(100) < config.getMutationRate() * config.getServiceNumber() * 100) {
                    individual.set(i, false);
                    int classId = i % config.getClassNumber();
                    int serviceId = random.nextInt(config.getServiceNumber());
                    individual.set(serviceId * config.getClassNumber() + classId, true);
                }
            }
        }
    }
}
