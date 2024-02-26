package tmp.uqam.stage.genetic.criteria;

import tmp.uqam.stage.genetic.subjects.Individual;

/**
 * Interface for the fitnessMethod (for now there is only one but in case of the multi objective one
 */
public interface FitnessMethod {

    /**
     * sets the fitness for an individual
     */
    void calculateFitness(Individual individual);

    /**
     * gets the base value of the fitness
     */
    double getBaseFitness();
}
