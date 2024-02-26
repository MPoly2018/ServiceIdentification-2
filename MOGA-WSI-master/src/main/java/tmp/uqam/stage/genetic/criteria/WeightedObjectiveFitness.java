package tmp.uqam.stage.genetic.criteria;

import tmp.uqam.stage.genetic.subjects.Individual;

/**
 * Calculation of fitness based on weights of managerial goals
 */
public class WeightedObjectiveFitness implements FitnessMethod {

    /**
     * Calculate fitness of an individual by applying every function of the managerial goal to it and balancing it
     * with the weight of each function
     *
     * @param individual the individual to calculate fitness on
     */
    @Override
    public void calculateFitness(Individual individual) {
        float fitness = 0;
        for (ManagerialGoal mg : ManagerialGoal.values()) {
            double value = mg.calculateObjectiveQuality(individual);
            fitness += value * mg.getWeight();
        }
        individual.setFitness(fitness);
    }

    /**
     * Initialize a base fitness for the individual
     * @return the base fitness 0.0
     */
    @Override
    public double getBaseFitness() {
        return 0.0;
    }

}
