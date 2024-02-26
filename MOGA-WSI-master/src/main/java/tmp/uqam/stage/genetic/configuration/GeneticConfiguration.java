package tmp.uqam.stage.genetic.configuration;

import tmp.uqam.stage.genetic.criteria.*;
import tmp.uqam.stage.genetic.operations.Crossover;
import tmp.uqam.stage.genetic.operations.Mutation;
import tmp.uqam.stage.graph.structure.DependencyGraph;

import java.util.EnumMap;
import java.util.Map;

/**
 * Singleton to store the configuration of the genetic algorithm
 */
public class GeneticConfiguration {

    private static GeneticConfiguration INSTANCE = new GeneticConfiguration();

    private int popSize;
    private double mutationRate;
    private double crossoverRate;
    private int eliteNumber;
    private int phenotypeSize;
    private int tournamentSize;
    private DependencyGraph graph;
    private int classNumber;
    private int serviceNumber;
    private FitnessMethod fitnessMethod;
    private Selection selection;

    private GeneticConfiguration() {
        popSize = 250;
        mutationRate = 0.05;
        crossoverRate = 0.75;
        eliteNumber = 5;
        phenotypeSize = 0;
        tournamentSize = 5;
        fitnessMethod = new WeightedObjectiveFitness();
        selection = new WeightedObjectiveSelection(new Crossover(), new Mutation());
        setWeights(1, 1, 1, 1, 1);
    }

    public static GeneticConfiguration getConfig() {
        return INSTANCE;
    }

    public int getPopSize() {
        return popSize;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public int getEliteNumber() {
        return eliteNumber;
    }

    public void setEliteNumber(int eliteNumber) {
        this.eliteNumber = eliteNumber;
    }

    public int getPhenotypeSize() {
        return phenotypeSize;
    }

    public void setPhenotypeSize(int phenotypeSize) {
        this.phenotypeSize = phenotypeSize;
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setGraph(DependencyGraph graph) {
        this.graph = graph;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    public int getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(int serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public FitnessMethod getFitnessMethod() {
        return fitnessMethod;
    }

    public Selection getSelection() {
        return selection;
    }

    /**
     * Normalize the weights for the sum to be 1
     */
    public void setWeights(double cohesion, double coupling, double complexity, double componentCount, double componentSize) {
        double sum = cohesion + coupling + complexity + componentCount + componentSize;
        ManagerialGoal.COHESION.setWeight(cohesion / sum);
        ManagerialGoal.COUPLING.setWeight(coupling / sum);
        ManagerialGoal.COMPLEXITY.setWeight(complexity / sum);
        ManagerialGoal.COMPONENT_COUNT.setWeight(componentCount / sum);
        ManagerialGoal.COMPONENT_SIZE.setWeight(componentSize / sum);
    }

    public Map<ManagerialGoal, Double> getWeights() {
        Map<ManagerialGoal, Double> weights = new EnumMap<>(ManagerialGoal.class);
        for (ManagerialGoal mg : ManagerialGoal.values()) {
            weights.put(mg, mg.getWeight());
        }
        return weights;
    }
}
