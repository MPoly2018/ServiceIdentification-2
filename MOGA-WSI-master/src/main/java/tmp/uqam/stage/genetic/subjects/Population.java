package tmp.uqam.stage.genetic.subjects;

import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.criteria.FitnessMethod;
import tmp.uqam.stage.genetic.operations.Mutation;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.*;

/**
 * Population of individuals, represents the list of all our architecture proposals for an iteration
 */
public class Population {

    private List<Individual> individuals;
    private GeneticConfiguration config = GeneticConfiguration.getConfig();

    /**
     * Creates an empty population
     */
    public Population() {
        this.individuals = new ArrayList<>(config.getPopSize());
    }

    /**
     * Creates a population with a fixed getNbWS for tournaments
     *
     * @param size the size of this population
     */
    public Population(int size) {
        this.individuals = new ArrayList<>(size);
    }

    /**
     * Initialize all individuals in thie population with the slicing provided
     *
     * @param slicing the architecture proposal
     */
    public Population(WSSlicing slicing) {
        this.individuals = new ArrayList<>(config.getPopSize());
        for (int i = 0; i < config.getPopSize(); i++) {
            individuals.add(new Individual(slicing));
        }
        apply(config.getFitnessMethod());
        for (Individual i : individuals) {
            new Mutation().smartMutate(i);
        }
    }

    /**
     * Compute the fitness value in parallel for each individual (slower for small and medium datasets)
     *
     * @param f the fitnessMethod to apply
     */
    public void apply(FitnessMethod f) {
        individuals.stream().parallel().forEach(f::calculateFitness);
    }

    /**
     * Return the individual with the biggest fitness for this population
     *
     * @return the fittest
     */
    public Individual fittest() {
        return Collections.max(individuals, Comparator.comparingDouble(Individual::getFitness));
    }

    /**
     * Saves a number of elites (fittest individuals) as defined in the configuration
     *
     * @return a new Population of elites
     */
    public Population saveElites() {
        Collections.sort(individuals);
        Population elites = new Population(config.getEliteNumber());
        int newPopSize = individuals.size() - config.getEliteNumber();
        for (int i = individuals.size() - 1; i >= newPopSize; i--) {
            elites.add(individuals.get(i));
        }
        return elites;
    }

    /**
     * Resize the population to the new configuration size wanted
     * if it is smaller we trim the individuals with the lowest fitness
     * if it is greater we duplicate random individuals to fit
     */
    public void resize() {
        if (config.getPopSize() < getPopSize()) {
            individuals.sort(Comparator.comparingDouble(Individual::getFitness));
            individuals.subList(0, getPopSize() - config.getPopSize()).clear();
        } else {
            Random r = new Random();
            int remaining = config.getPopSize() - getPopSize();
            for (int i = 0; i < remaining; i++) {
                individuals.add(new Individual(individuals.get(r.nextInt(getPopSize()))));
            }
        }
    }

    public void add(Individual i) {
        individuals.add(i);
    }

    public int getPopSize() {
        return individuals.size();
    }

    public Individual get(int i) {
        return individuals.get(i);
    }

    @Override
    public String toString() {
        return "Population :\n" + individuals;
    }


    /////////// Test ////////

    public List<Individual> extractPopulation() {
        return individuals;
    }

    public Population(Random r) {
        individuals = new ArrayList<>(config.getPopSize());
        for (int i = 0; i < config.getPopSize(); i++) {
            individuals.add(new Individual(r));
        }
        apply(config.getFitnessMethod());
    }
}
