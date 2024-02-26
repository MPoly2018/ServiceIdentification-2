package tmp.uqam.stage.genetic.criteria;

import tmp.uqam.stage.genetic.subjects.Individual;

public interface ObjectiveFunction {

    /**
     * Interface implemented in the ManagerialGoal enumeration, makes it possible to calculate the value of an individual
     * for an objective function
     */
    double calculateObjectiveQuality(Individual individual);
}
