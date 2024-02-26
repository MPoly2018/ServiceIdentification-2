package tmp.uqam.stage.genetic.criteria;

import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.subjects.Individual;
import tmp.uqam.stage.graph.structure.DependencyGraph;
import tmp.uqam.stage.slicing.ClassSlicing;
import tmp.uqam.stage.slicing.ClassVertex;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Enumeration of all the functions we use to define a fitness,
 * they each overload calculateObjectiveQuality which calculate a nozmalized fitness
 * for an individual
 * each is assigned a weight, by default 0.2.
 */
public enum ManagerialGoal implements ObjectiveFunction {

    /**
     * COHESION / EASE OF ASSEMBLY: Computes the internal strength of components by making a sum
     * of their internal connections
     */
    COHESION {
        @Override
        public double calculateObjectiveQuality(Individual individual) {
            DependencyGraph graph = getConfig().getGraph();
            double value = 0;
            for (ClassSlicing classes : individual.getPhenotypeAsWSSlicing()) {
                value += graph.getInterWeight(classes);
            }
            value = normalize(value, 0, getConfig().getGraph().getTotalWeight());
            return value;
        }
    },
    /**
     * COUPLING / COST EFFECTIVENESS : computes the dependencies between the classes of a component and the outside world
     */
    COUPLING {
        @Override
        public double calculateObjectiveQuality(Individual individual) {
            DependencyGraph graph = getConfig().getGraph();
            double value = 0;
            for (ClassSlicing classes : individual.getPhenotypeAsWSSlicing()) {
                value += graph.getIntraWeight(classes);
            }
            value = normalize(value, getConfig().getGraph().getTotalWeight(), 0);
            return value;
        }
    },
    /**
     * COMPLEXITY / MAINTENABILITY : Asserts that the complexity in method number of each component is
     * almost equal
     */
    COMPLEXITY {
        @Override
        public double calculateObjectiveQuality(Individual individual) {
            List<Integer> complexityByWS = new ArrayList<>();
            for (ClassSlicing classes : individual.getPhenotypeAsWSSlicing()) {
                int nbMeth = 0;
                for (ClassVertex cv : classes) {
                    nbMeth += cv.getNbMethods();
                }
                complexityByWS.add(nbMeth);
            }
            int total = getConfig().getGraph().getTotalMethods();
            double mean = (double) total / getConfig().getServiceNumber();
            double deviance = complexityByWS.stream().mapToDouble(complexity -> Math.abs(complexity - mean)).sum();
            deviance = normalize(deviance, ((double) total * getConfig().getServiceNumber()) / 3, 0);
            return deviance;
        }
    },
    /**
     * COMPONENT_COUNT / CUSTOMIZATION : Asserts that all components are present and that they are as less as possible
     * replicated among multiple services
     */
    COMPONENT_COUNT {
        @Override
        public double calculateObjectiveQuality(Individual individual) {
            double wanted = getConfig().getClassNumber();
            ClassSlicing allClasses = new ClassSlicing(new HashSet<>());
            WSSlicing slicing = individual.getPhenotypeAsWSSlicing();
            // Assert that all classes are present or return a very low value
            for (ClassSlicing classes : slicing) {
                allClasses.addAll(classes);
            }
            if (allClasses.size() != wanted) {
                return -Math.abs(allClasses.size() - wanted);
            }

            // Check that all classes are present only once, if not reduce a bit the fitness
            int totalClasses = 0;
            for (ClassSlicing classes : slicing) {
                totalClasses += classes.size();
            }
            double deviance = Math.abs(totalClasses - wanted);
            deviance = normalize(deviance, wanted, 0);

            // Asserts that we have the right number of services, if not reduce drastically fitness
            for (ClassSlicing classes : slicing) {
                if (classes.size() == 0) {
                    deviance -= 1;
                }
            }
            return deviance;
        }
    },
    /**
     * COMPONENT_SIZE / REUSABILITY : computes the difference between the average size and the size of each component
     */
    COMPONENT_SIZE {
        @Override
        public double calculateObjectiveQuality(Individual individual) {
            double wanted = (double) getConfig().getClassNumber() / getConfig().getServiceNumber();
            double difference = 0;
            for (ClassSlicing classes : individual.getPhenotypeAsWSSlicing()) {
                difference += Math.abs(wanted - classes.size());
            }
            difference = normalize(difference, ((double) getConfig().getClassNumber() * getConfig().getServiceNumber()) / 3, 0);
            return difference;
        }
    };

    private double weight;

    ManagerialGoal() {
        this.weight = 0.2;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Normalize a double value between 0 and 1
     * if there is overflow it is still reduced to this interval
     *
     * @param value the value to normalize
     * @param min   lower bound of the value
     * @param max   higher bound of the value
     * @return the normalized data between 0 and 1
     */
    public static double normalize(double value, double min, double max) {
        double res = (value - min) / (max - min);
        if (res < 0) {
            res = 0;
        } else if (res > 1) {
            res = 1;
        }
        return res;
    }

    private static GeneticConfiguration getConfig() {
        return GeneticConfiguration.getConfig();
    }

    @Override
    public String toString() {
        return "ManagerialGoal{" +
                "name=" + this.name() +
                ", weight=" + weight +
                '}';
    }
}
