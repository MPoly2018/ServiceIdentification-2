package tmp.uqam.stage.genetic.subjects;

import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.criteria.ManagerialGoal;
import tmp.uqam.stage.slicing.ClassSlicing;
import tmp.uqam.stage.slicing.ClassVertex;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.*;

/**
 * Individual for the simulation, a list of webservices and the classes they have in a binary representation
 */
public class Individual implements Comparable<Individual> {

    private boolean[] phenotype;
    private double fitness;
    private GeneticConfiguration config;

    /**
     * Creates an empty individual
     */
    public Individual() {
        this.config = GeneticConfiguration.getConfig();
        this.fitness = config.getFitnessMethod().getBaseFitness();
        int phenotypeSize = config.getPhenotypeSize();
        phenotype = new boolean[phenotypeSize];
    }


    /**
     * Creates an individual based on a WSSlicing
     *
     * @param slicing the representation to generate from
     */
    public Individual(WSSlicing slicing) {
        this.config = GeneticConfiguration.getConfig();
        this.fitness = config.getFitnessMethod().getBaseFitness();
        phenotype = slicing.getBinaryRepresentation();
    }

    /**
     * Clones an individual
     */
    public Individual(Individual individual) {
        this.config = GeneticConfiguration.getConfig();
        this.fitness = config.getFitnessMethod().getBaseFitness();
        this.phenotype = individual.phenotype.clone();
    }

    /**
     * Get the binary phenotype as a Slicing to facilitate operations on it
     *
     * @return WSSlicing alternate representation of invidividual phenotype
     */
    public WSSlicing getPhenotypeAsWSSlicing() {
        WSSlicing webservices = new WSSlicing();
        for (int i = 0; i < config.getServiceNumber(); i++) {
            Set<ClassVertex> classes = new HashSet<>();
            for (int j = 0; j < config.getClassNumber(); j++) {
                if (phenotype[i * config.getClassNumber() + j]) {
                    classes.add(config.getGraph().getClassVertex((i * config.getClassNumber() + j) % config.getClassNumber()));
                }
            }
            webservices.add(new ClassSlicing(classes));
        }
        return webservices;
    }

    /**
     * Get detailed info on the fitness of a specific individual (the fittest for example)
     */
    public String getDetailedFitnessInfo() {
        StringBuilder sb = new StringBuilder("FITTEST FITNESS : ").append(String.format("%.3f", this.fitness)).append("\n");
        int i = 0;
        for (ManagerialGoal mg : ManagerialGoal.values()) {
            sb.append(mg.name()).append(" : ").append(String.format("%.3f", mg.calculateObjectiveQuality(this))).append("\t");
            if (++i % 2 == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Compare fitness value of two individuals
     *
     * @param i the second individual to compare to
     * @return negative int if smaller, 0 if equals and positive int if bigger
     */
    @Override
    public int compareTo(Individual i) {
        return Double.compare(this.getFitness(), (i.getFitness()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Individual that = (Individual) o;
        return Objects.equals(fitness, that.fitness);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fitness, config);
        result = 31 * result + Arrays.hashCode(phenotype);
        return result;
    }

    public boolean get(int i) {
        return phenotype[i];
    }

    public void set(int i, boolean value) {
        phenotype[i] = value;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitnessValue) {
        this.fitness = fitnessValue;
    }

    @Override
    public String toString() {
        return "phenotype=" + this.phenotypeServiceRepresentation() +
                ", fitness=" + fitness +
                '\n';
    }

    private String phenotypeServiceRepresentation() {
        return this.getPhenotypeAsWSSlicing().toString();
    }

    ////////////////// TEST /////////////////:

    public boolean[] extractPhenotype() {
        return phenotype.clone();
    }

    public Individual(Random r) {
        this();
        int phenotypeSize = config.getPhenotypeSize();
        phenotype = new boolean[phenotypeSize];
        for (int i = 0; i < phenotypeSize; i++) {
            phenotype[i] = r.nextBoolean();
        }
    }
}
