package tmp.uqam.stage.genetic.criteria;

import tmp.uqam.stage.genetic.subjects.Population;

public interface Selection {

    /**
     * Interface for the selection, for now there is only one ean of selection which is weightedObjective
     *
     * @param population the population to select from
     * @return the new population for the next generation
     */
    Population select(Population population);
}
