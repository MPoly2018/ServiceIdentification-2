package tmp.uqam.stage.genetic;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.subjects.Individual;
import tmp.uqam.stage.genetic.subjects.Population;
import tmp.uqam.stage.slicing.WSSlicing;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvolutionEngine {

    private Population population;
    private int counter;

    public EvolutionEngine(WSSlicing slicings) {
        counter = 0;
        population = new Population(slicings);
    }

    public Individual step(int numberStep, ProgressBar progressBar) {
        if (population.getPopSize() != GeneticConfiguration.getConfig().getPopSize()) {
            population.resize();
        }
        for (int i = 0; i < numberStep; i++) {
            double step = i + 1;
            iterate();
            Logger.getLogger(getClass().getName()).log(Level.INFO, population.toString());
            Individual fittest = population.fittest();
            Logger.getLogger(getClass().getName()).log(Level.INFO, "--------------------\nGENERATION : " + counter++);
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Fittest : " + fittest);
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Mean fitness : " + getPopulation().extractPopulation().stream().mapToDouble(Individual::getFitness).average().getAsDouble());
            Platform.runLater(() -> progressBar.setProgress(step / numberStep));
        }
        return population.fittest();
    }

    public void iterate() {
        population = GeneticConfiguration.getConfig().getSelection().select(population);
    }


    ///////// TEST //////////////

    public EvolutionEngine() {
        Random r = new Random();
        population = new Population(r);
    }

    public Population getPopulation() {
        return population;
    }
}
