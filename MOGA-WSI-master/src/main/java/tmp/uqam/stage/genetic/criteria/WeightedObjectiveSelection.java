package tmp.uqam.stage.genetic.criteria;

import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.operations.Crossover;
import tmp.uqam.stage.genetic.operations.Mutation;
import tmp.uqam.stage.genetic.subjects.Individual;
import tmp.uqam.stage.genetic.subjects.Population;

import java.util.Random;

/**
 * Creates the new population by improving the last one in a weighed objective way
 */
public class WeightedObjectiveSelection implements Selection {

    private Crossover crossover;
    private Mutation mutation;

    public WeightedObjectiveSelection(Crossover crossover, Mutation mutation) {
        this.crossover = crossover;
        this.mutation = mutation;
    }

    /**
     * The selection process
     *
     * @param population the population to select from
     * @return the new population
     */
    @Override
    public Population select(Population population) {

        // Elite salvation
        Population nextGen = population.saveElites();

        // Crossover
        Random random = new Random();
        for (int i = 0; i < population.getPopSize() - GeneticConfiguration.getConfig().getEliteNumber() - 1; i += 2) {
            if (random.nextInt(100) < GeneticConfiguration.getConfig().getCrossoverRate() * 100) {
                Individual i1 = tournamentSelection(population);
                Individual i2 = tournamentSelection(population);
                Individual newI = crossover.onePointCrossover(i1, i2);
                Individual newI2 = crossover.onePointCrossover(i2, i1);
                nextGen.add(newI);
                nextGen.add(newI2);
            }
        }

        // Salvation of remaining individuals to keep pop size
        int remaining = population.getPopSize() - nextGen.getPopSize();
        for (int i = 0; i < remaining; i++) {
            Individual individual = tournamentSelection(population);
            nextGen.add(new Individual(individual));
        }

        // Mutation
        for (int i = GeneticConfiguration.getConfig().getEliteNumber(); i < nextGen.getPopSize(); i++) {
            mutation.smartMutate(nextGen.get(i));
        }

        // Computing of new fitness
        nextGen.apply(GeneticConfiguration.getConfig().getFitnessMethod());

        return nextGen;
    }

    /**
     * Selection of the fittest semi random,
     * By using a subset of the population we get the local fittests individual to do a onePointCrossover
     *
     * @param population subset of the population
     * @return the fittest of this subset
     */
    private Individual tournamentSelection(Population population) {
        int tournamentSize = GeneticConfiguration.getConfig().getTournamentSize();
        Population tournament = new Population(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = new Random().nextInt(population.getPopSize() - 1);
            tournament.add(population.get(randomId));
        }
        tournament.apply(GeneticConfiguration.getConfig().getFitnessMethod());
        return tournament.fittest();
    }
}
